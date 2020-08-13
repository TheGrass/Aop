package de.imcq.aop.annotation;

import de.imcq.aop.component.Advice;
import de.imcq.aop.core.AdviceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * sample
 * <pre>
 *     public class SampleAdvice implements BeforeAdvice, AfterAdvice, FinallyAdvice, ThrowableAdvice {
 *         @Override
 *         public void afterAdvice(AdviceContext context) {}
 *         @Override
 *         public void beforeAdvice(AdviceContext context) {}
 *         @Override
 *         public void finallyAdvice(AdviceContext context) {}
 *         @Override
 *         public void throwableAdvice(AdviceContext context) {}
 *     }
 * </pre>
 * <pre>
 *     @JoinPoint(SampleAdvice.class)
 *     public class Sample {
 *         private void sample1() {}
 *
 *         @JoinPoint.Ignore
 *         private void sample2() {}
 *
 *         @JoinPoint(AnotherAdvice.class)
 *         private void sample3() {}
 *     }
 * </pre>
 * <pre>
 *     public class Sample {
 *         @JoinPoint(SampleAdvice.class)
 *         private void sample() {}
 *     }
 * </pre>
 *
 * @author CQ
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface JoinPoint {

    /**
     * 指定 Advice 的实现类
     */
    Class<? extends Advice> value();

    AdviceType[] support() default {AdviceType.BEFORE, AdviceType.AFTER, AdviceType.FINALLY, AdviceType.THROWABLE};

    /**
     * 忽略
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Ignore {

    }
}
