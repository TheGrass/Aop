package de.imcq.aop.core;

import de.imcq.aop.component.Advice;
import de.imcq.aop.annotation.JoinPoint;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Advice 上下文
 *
 * @author CQ
 */
public final class AdviceContext {
    /**
     * 共享变量
     */
    private Object shared;
    /**
     * JoinPoint 方法上下文栈
     *
     * @see JoinPoint
     */
    private Deque<Context> contextStack = new ArrayDeque<>(4);
    /**
     * 当前方法上下文
     */
    private Context currentContext;


    /**
     * 添加一个Context
     */
    protected void push(Advice advice, Class<?> targetClass, String methodName, Object... args) {
        Context context = new Context(advice);
        context.setTargetClass(targetClass);
        context.setMethodName(methodName);
        context.setArgs(args);
        contextStack.addFirst(context);
        currentContext = context;
    }


    public Object getShared() {
        return shared;
    }

    public void setShared(Object shared) {
        this.shared = shared;
    }

    public Class<?> getTargetClass() {
        return currentContext.targetClass;
    }

    public String getMethodName() {
        return currentContext.methodName;
    }

    public Object getReturnValue() {
        return currentContext.returnValue;
    }

    public Object[] getArgs() {
        return currentContext.args;
    }

    public Throwable getThrowable() {
        return currentContext.throwable;
    }

    public long getStartTick() {
        return currentContext.startTick;
    }

    protected Context getCurrentContext() {
        return currentContext;
    }

    protected void pop() {
        contextStack.removeFirst();
        currentContext = contextStack.peekFirst();
    }

    protected void proceed() {
        currentContext.advice.exec(this);
    }

    protected AdviceType getCurrentType() {
        return currentContext.currentType;
    }

    final class Context {

        private Advice advice;
        /**
         * 当前类型
         */
        private AdviceType currentType;
        /**
         * 开始执行时间
         */
        private long startTick;
        /**
         * 执行方法所属类名
         */
        private Class<?> targetClass;
        /**
         * 执行方法名
         */
        private String methodName;
        /**
         * 方法参数
         */
        private Object[] args;
        /**
         * 方法返回值
         */
        private Object returnValue;
        /**
         * 捕获的异常
         */
        private Throwable throwable;

        public Context(Advice advice) {
            this.advice = advice;
            this.currentType = AdviceType.BEFORE;
            this.startTick = System.nanoTime();
        }

        protected void setCurrentType(AdviceType currentType) {
            this.currentType = currentType;
        }

        protected void setTargetClass(Class<?> targetClass) {
            this.targetClass = targetClass;
        }

        protected void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        protected void setReturnValue(Object returnValue) {
            this.returnValue = returnValue;
        }

        protected void setArgs(Object... args) {
            this.args = args;
        }

        protected void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }
    }
}
