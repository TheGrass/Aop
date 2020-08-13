package de.imcq.aop.core;


import de.imcq.aop.component.*;
import de.imcq.aop.exception.AdviceRuntimeException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理类
 *
 * @author CQ
 * @see Advice
 */
public class AdviceProxy implements InvocationHandler {

    private static final MethodType ADVICE_EXEC_METHOD_TYPE = MethodType.methodType(void.class, AdviceContext.class);
    private static final String ADVICE_EXEC_METHOD_NAME = "exec";

    private static final String BEFORE_ADVICE_NAME = "beforeAdvice";
    private static final String AFTER_ADVICE_NAME = "afterAdvice";
    private static final String THROWABLE_ADVICE_NAME = "throwableAdvice";
    private static final String FINALLY_ADVICE_NAME = "finallyAdvice";

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static MethodHandle BEFORE_ADVICE_METHOD;
    private static MethodHandle AFTER_ADVICE_METHOD;
    private static MethodHandle THROWABLE_ADVICE_METHOD;
    private static MethodHandle FINALLY_ADVICE_METHOD;

    static {
        try {
            BEFORE_ADVICE_METHOD = LOOKUP.findVirtual(BeforeAdvice.class, BEFORE_ADVICE_NAME, ADVICE_EXEC_METHOD_TYPE);
            AFTER_ADVICE_METHOD = LOOKUP.findVirtual(AfterAdvice.class, AFTER_ADVICE_NAME, ADVICE_EXEC_METHOD_TYPE);
            THROWABLE_ADVICE_METHOD = LOOKUP.findVirtual(ThrowableAdvice.class, THROWABLE_ADVICE_NAME, ADVICE_EXEC_METHOD_TYPE);
            FINALLY_ADVICE_METHOD = LOOKUP.findVirtual(FinallyAdvice.class, FINALLY_ADVICE_NAME, ADVICE_EXEC_METHOD_TYPE);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AdviceRuntimeException(e);
        }
    }

    private Advice target;

    public AdviceProxy(Advice target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals(ADVICE_EXEC_METHOD_NAME)) {
            AdviceContext context = (AdviceContext) args[0];
            switch (context.getCurrentType()) {
                case BEFORE:
                    return target instanceof BeforeAdvice ? BEFORE_ADVICE_METHOD.invokeWithArguments(target, context) : null;
                case AFTER:
                    return target instanceof AfterAdvice ? AFTER_ADVICE_METHOD.invokeWithArguments(target, context) : null;
                case THROWABLE:
                    return target instanceof ThrowableAdvice ? THROWABLE_ADVICE_METHOD.invokeWithArguments(target, context) : null;
                case FINALLY:
                    return target instanceof FinallyAdvice ? FINALLY_ADVICE_METHOD.invokeWithArguments(target, context) : null;
                default:
                    return null;
            }
        }
        return method.invoke(target, args);
    }
}
