package de.imcq.aop.compile;

import de.imcq.aop.annotation.JoinPoint;
import de.imcq.aop.core.AdviceContextHolder;
import de.imcq.aop.core.ParamNames;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import java.util.Collections;
import java.util.Set;

/**
 * @author CQ
 */
abstract class AbstractJoinPointProcessor extends AbstractProcessor {

    /**
     * tools
     */
    protected JavacTrees trees;
    protected TreeMaker treeMaker;
    protected Names names;
    /**
     * method
     */
    protected JCTree.JCExpression newContextMethod;
    protected JCTree.JCExpression toAfterMethod;
    protected JCTree.JCExpression toThrowableMethod;
    /**
     * exec
     */
    protected JCTree.JCExpressionStatement contextProceedExec;
    protected JCTree.JCExpressionStatement contextReleaseExec;
    protected JCTree.JCExpressionStatement toFinallyExec;
    protected JCTree.JCExpressionStatement toVoidAfterExec;

    /**
     * type
     */
    protected JCTree.JCExpression throwableType;


    /**
     * @param classElement  类
     * @param methodElement 方法
     * @param joinPoint     注解
     */
    protected abstract void process(Element classElement, Element methodElement, JoinPoint joinPoint);

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        initMethodAndType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(JoinPoint.class);
        for (Element element : annotatedElements) {
            JoinPoint joinPoint = element.getAnnotation(JoinPoint.class);
            if (joinPoint.support().length == 0) {
                continue;
            }
            ElementKind elementKind = element.getKind();
            if (elementKind.isClass() || elementKind.isInterface()) {
                for (Element member : element.getEnclosedElements()) {
                    if (member.getKind() != ElementKind.METHOD || member.getAnnotation(JoinPoint.Ignore.class) != null ||
                            member.getAnnotation(JoinPoint.class) != null || isAbstract(member)) {
                        continue;
                    }
                    process(element, member, joinPoint);
                }
            } else {
                if (isAbstract(element)) {
                    continue;
                }
                process(element.getEnclosingElement(), element, joinPoint);
            }
        }
        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(JoinPoint.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * init
     */
    private void initMethodAndType() {
        toAfterMethod = resolveExpression(AdviceContextHolder.TO_AFTER_METHOD_NAME);
        toThrowableMethod = resolveExpression(AdviceContextHolder.TO_THROWABLE_METHOD_NAME);
        newContextMethod = resolveExpression(AdviceContextHolder.NEW_CONTEXT_METHOD_NAME);
        contextProceedExec = treeMaker.Exec(buildMethodInvoke(resolveExpression(AdviceContextHolder.PROCEED_METHOD_NAME), List.nil()));
        contextReleaseExec = treeMaker.Exec(buildMethodInvoke(resolveExpression(AdviceContextHolder.RELEASE_METHOD_NAME), List.nil()));
        toFinallyExec = treeMaker.Exec(buildMethodInvoke(resolveExpression(AdviceContextHolder.TO_FINALLY_METHOD_NAME), List.nil()));
        toVoidAfterExec = treeMaker.Exec(buildMethodInvoke(resolveExpression(AdviceContextHolder.TO_VOID_AFTER_METHOD_NAME), List.nil()));
        throwableType = resolveExpression(Throwable.class.getName());
    }

    /**
     * @param element
     * @return element is abstract
     */
    protected boolean isAbstract(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        for (Modifier modifier : modifiers) {
            if (Modifier.ABSTRACT == modifier) {
                return true;
            }
        }
        return false;
    }

    protected JCTree.JCExpression getAdviceClass(JoinPoint joinPoint) {
        try {
            joinPoint.value();
        } catch (MirroredTypeException ex) {
            String className = ex.getTypeMirror().toString();
            return resolveExpression(className + ".class");
        }
        return null;
    }

    /**
     * 解析表达式
     *
     * @param expression
     * @return resolved
     */
    protected JCTree.JCExpression resolveExpression(String expression) {
        String[] itemArray = expression.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(itemArray[0]));
        for (int i = 1; i < itemArray.length; i++) {
            expr = treeMaker.Select(expr, names.fromString(itemArray[i]));
        }
        return expr;
    }

    /**
     * 构建方法调用
     *
     * @return
     */
    protected JCTree.JCMethodInvocation buildMethodInvoke(JCTree.JCExpression method, List<JCTree.JCExpression> params) {
        return treeMaker.Apply(List.nil(), method, params);
    }


    /**
     * 克隆方法
     *
     * @param source
     * @return
     */
    protected JCTree.JCMethodDecl cloneAndRenameMethod(JCTree.JCMethodDecl source) {
        //clone
        JCTree.JCMethodDecl clonedMethod = (JCTree.JCMethodDecl) source.clone();
        clonedMethod.mods = (JCTree.JCModifiers) source.mods.clone();
        source.mods.annotations = List.nil();
        List<JCTree.JCVariableDecl> vars = List.nil();
        for (JCTree.JCVariableDecl jcVariableDecl : source.params) {
            JCTree.JCVariableDecl param = (JCTree.JCVariableDecl) jcVariableDecl.clone();
            param.mods = (JCTree.JCModifiers) jcVariableDecl.mods.clone();
            vars = vars.append(param);
            jcVariableDecl.mods.annotations = List.nil();
        }
        clonedMethod.params = vars;
        //rename
        Name repName = names.fromString(ParamNames.getParamName());
        source.name = repName;
        return clonedMethod;
    }
}
