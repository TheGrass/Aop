package de.imcq.aop.core;

import de.imcq.aop.component.Advice;

/**
 * @author CQ
 */
public final class AdviceContextHolder {

    private static final AbstractAdviceFactory factory = AdviceFactoryLoader.load();
    private static final String CLASS_NAME = AdviceContextHolder.class.getName();
    public static final String NEW_CONTEXT_METHOD_NAME = CLASS_NAME + ".createNew";
    public static final String TO_AFTER_METHOD_NAME = CLASS_NAME + ".toAfter";
    public static final String TO_VOID_AFTER_METHOD_NAME = CLASS_NAME + ".toVoidAfter";
    public static final String TO_FINALLY_METHOD_NAME = CLASS_NAME + ".toFinally";
    public static final String TO_THROWABLE_METHOD_NAME = CLASS_NAME + ".toThrowable";
    public static final String PROCEED_METHOD_NAME = CLASS_NAME + ".proceed";
    public static final String RELEASE_METHOD_NAME = CLASS_NAME + ".release";

    private static final ThreadLocal<AdviceContext> CONTEXT_HOLDER = ThreadLocal.withInitial(() -> new AdviceContext());

    /**
     * create advice context
     */
    public static void createNew(Class<? extends Advice> adviceClass, Class<?> targetClass, String methodName, Object... args) {
        Advice advice = factory.get(adviceClass);
        AdviceContext adviceContext = CONTEXT_HOLDER.get();
        adviceContext.push(advice, targetClass, methodName, args);
    }


    public static void toAfter(Object returnVal) {
        AdviceContext.Context context = CONTEXT_HOLDER.get().getCurrentContext();
        context.setCurrentType(AdviceType.AFTER);
        context.setReturnValue(returnVal);
    }

    public static void toVoidAfter() {
        CONTEXT_HOLDER.get().getCurrentContext().setCurrentType(AdviceType.AFTER);
    }


    public static void toFinally() {
        CONTEXT_HOLDER.get().getCurrentContext().setCurrentType(AdviceType.FINALLY);
    }

    public static void toThrowable(Throwable throwable) {
        AdviceContext.Context context = CONTEXT_HOLDER.get().getCurrentContext();
        context.setCurrentType(AdviceType.THROWABLE);
        context.setThrowable(throwable);
    }

    public static void proceed() {
        CONTEXT_HOLDER.get().proceed();
    }


    public static void release() {
        AdviceContext context = CONTEXT_HOLDER.get();
        context.pop();
        if (context.getCurrentContext() == null) {
            CONTEXT_HOLDER.remove();
        }
    }


    private AdviceContextHolder() {
    }
}
