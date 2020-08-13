package de.imcq.aop.component;

import de.imcq.aop.core.AdviceContext;

/**
 * @author CQ
 */
public interface Advice {
    /**
     * 执行入口
     *
     * @param context
     */
    default void exec(AdviceContext context) {
    }
}
