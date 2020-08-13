package de.imcq.aop.compile;

import com.google.auto.service.AutoService;
import de.imcq.aop.annotation.JoinPoint;
import de.imcq.aop.core.AdviceType;
import de.imcq.aop.core.ParamNames;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;

/**
 * @author CQ
 */
@AutoService(Processor.class)
public class JoinPointProcessor extends AbstractJoinPointProcessor {

    @Override
    protected void process(Element classElement, Element methodElement, JoinPoint joinPoint) {
        JCTree.JCClassDecl classTree = (JCTree.JCClassDecl) trees.getTree(classElement);
        //原始方法
        JCTree.JCMethodDecl originalMethodDecl = (JCTree.JCMethodDecl) trees.getTree(methodElement);
        //复制的方法
        JCTree.JCMethodDecl clonedMethodDecl = cloneAndRenameMethod(originalMethodDecl);
        //body
        ListBuffer<JCTree.JCStatement> bodyStatements = new ListBuffer<>();
        //原始方法参数
        List<JCTree.JCExpression> originalMethodParamList = List.nil();
        for (JCTree.JCVariableDecl param : originalMethodDecl.params) {
            originalMethodParamList = originalMethodParamList.append(treeMaker.Ident(param.getName()));
        }
        //context
        JCTree.JCMethodInvocation createNewContextInvocation = buildMethodInvoke(newContextMethod, originalMethodParamList.prependList(List.of(getAdviceClass(joinPoint),
                resolveExpression(classElement.asType().toString() + ".class"), treeMaker.Literal(methodElement.getSimpleName().toString()))));
        bodyStatements.append(treeMaker.Exec(createNewContextInvocation));
        //before
        if (support(joinPoint, AdviceType.BEFORE)) {
            bodyStatements.append(contextProceedExec);
        }
        JCTree.JCMethodInvocation targetMethodInvocation = buildMethodInvoke(treeMaker.Ident(originalMethodDecl.name), originalMethodParamList);
        JCTree.JCBlock tryContent = buildTryContent(support(joinPoint, AdviceType.AFTER), bodyStatements, originalMethodDecl.restype, targetMethodInvocation);
        JCTree.JCTry jcTry = treeMaker.Try(tryContent, buildCatch(support(joinPoint, AdviceType.THROWABLE)), buildFinally(support(joinPoint, AdviceType.FINALLY)));
        bodyStatements.append(jcTry);
        clonedMethodDecl.body = treeMaker.Block(0, bodyStatements.toList());
        classTree.defs = classTree.defs.prepend(clonedMethodDecl);
    }

    /**
     * 构建try块
     */
    private JCTree.JCBlock buildTryContent(boolean supportAfter, ListBuffer<JCTree.JCStatement> bodyStatements,
                                           JCTree.JCExpression returnType, JCTree.JCMethodInvocation originalMethodApply) {
        ListBuffer<JCTree.JCStatement> tryStatements = new ListBuffer<>();
        if (returnType.type.hasTag(TypeTag.VOID)) {
            tryStatements.append(treeMaker.Exec(originalMethodApply));
            if (supportAfter) {
                tryStatements.append(toVoidAfterExec);
                tryStatements.append(contextProceedExec);
            }
        } else {
            Name returnValName = names.fromString(ParamNames.getParamName());
            JCTree.JCIdent returnIdent = treeMaker.Ident(returnValName);
            JCTree.JCAssign returnAssign = treeMaker.Assign(returnIdent, originalMethodApply);
            JCTree.JCVariableDecl returnVar = treeMaker.VarDef(treeMaker.Modifiers(0), returnValName, returnType, null);
            bodyStatements.append(returnVar);
            tryStatements.append(treeMaker.Exec(returnAssign));
            if (supportAfter) {
                tryStatements.append(treeMaker.Exec(buildMethodInvoke(toAfterMethod, List.of(returnIdent))));
                tryStatements.append(contextProceedExec);
            }
            tryStatements.append(treeMaker.Return(returnIdent));
        }
        return treeMaker.Block(0, tryStatements.toList());
    }

    /**
     * 构建catch
     */
    private List<JCTree.JCCatch> buildCatch(boolean supportThrowable) {
        List<JCTree.JCCatch> catchList = List.nil();
        if (supportThrowable) {
            Name throwableValName = names.fromString(ParamNames.getParamName());
            JCTree.JCIdent throwableIdent = treeMaker.Ident(throwableValName);
            JCTree.JCVariableDecl throwableVar = treeMaker.VarDef(treeMaker.Modifiers(Flags.FINAL), throwableValName, throwableType, null);
            ListBuffer<JCTree.JCStatement> catchStatements = new ListBuffer<>();
            catchStatements.append(treeMaker.Exec(buildMethodInvoke(toThrowableMethod, List.of(throwableIdent))));
            catchStatements.append(contextProceedExec);
            catchStatements.append(treeMaker.Throw(throwableIdent));
            JCTree.JCCatch jcCatch = treeMaker.Catch(throwableVar, treeMaker.Block(0, catchStatements.toList()));
            catchList = catchList.append(jcCatch);
        }
        return catchList;
    }

    /**
     * 构建finally
     */
    private JCTree.JCBlock buildFinally(boolean supportFinally) {
        ListBuffer<JCTree.JCStatement> finallyStatements = new ListBuffer<>();
        if (supportFinally) {
            finallyStatements.append(toFinallyExec);
            finallyStatements.append(contextProceedExec);
        }
        finallyStatements.append(contextReleaseExec);
        return treeMaker.Block(0, finallyStatements.toList());
    }

    /**
     * 是否支持指定类型
     */
    private boolean support(JoinPoint joinPoint, AdviceType target) {
        for (AdviceType source : joinPoint.support()) {
            if (source == target) {
                return true;
            }
        }
        return false;
    }
}
