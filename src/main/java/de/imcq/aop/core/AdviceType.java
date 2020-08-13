package de.imcq.aop.core;

/**
 * 类型
 *
 * @author CQ
 */
public enum AdviceType {

    /**
     * 方法执行前
     */
    BEFORE,
    /**
     * 方法执行会后，返回前
     * 异常时不会执行
     */
    AFTER,
    /**
     * 异常时
     */
    THROWABLE,
    /**
     * 方法执行会后，返回前
     * 异常时会执行
     */
    FINALLY
}
