package de.imcq.aop.component;

import de.imcq.aop.core.AdviceContext;

/**
 * @author CQ
 */
public interface FinallyAdvice extends Advice {
    /**
     * finally
     *
     * @param context
     */
    void finallyAdvice(AdviceContext context);
}
