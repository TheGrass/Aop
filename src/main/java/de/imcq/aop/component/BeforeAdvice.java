package de.imcq.aop.component;

import de.imcq.aop.core.AdviceContext;

/**
 * 方法执行前
 *
 * @author CQ
 */
public interface BeforeAdvice extends Advice {
    /**
     * 方法执行前
     *
     * @param context
     */
    void beforeAdvice(AdviceContext context);
}
