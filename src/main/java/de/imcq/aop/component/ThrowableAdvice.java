package de.imcq.aop.component;

import de.imcq.aop.core.AdviceContext;

/**
 * @author CQ
 */
public interface ThrowableAdvice extends Advice {
    /**
     * 异常
     *
     * @param context
     */
    void throwableAdvice(AdviceContext context);
}
