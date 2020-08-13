package de.imcq.aop.core;

import de.imcq.aop.component.Advice;
import de.imcq.aop.exception.AdviceRuntimeException;
import de.imcq.aop.util.ObjectUtils;

/**
 * 默认实现类
 *
 * @author CQ
 * @see AbstractAdviceFactory
 */
public class DefaultAdviceFactory extends AbstractAdviceFactory {

    @Override
    protected Advice getAdviceInstance(Class<? extends Advice> adviceClass) {
        if (!isEffectiveAdviceClass(adviceClass)) {
            throw new AdviceRuntimeException(adviceClass + " is not an effective class");
        }
        return ObjectUtils.newInstance(adviceClass);
    }
}
