package de.imcq.aop.component;

import de.imcq.aop.core.AdviceContext;

/**
 * 方法执行后
 *
 * @author CQ
 */
public interface AfterAdvice extends Advice {

    /**
     * 方法执行后
     * 异常时不会执行
     *
     * @param context
     */
    void afterAdvice(AdviceContext context);
}
