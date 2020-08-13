package de.imcq.aop.core;

import de.imcq.aop.annotation.DisableCache;
import de.imcq.aop.component.Advice;
import de.imcq.aop.exception.AdviceRuntimeException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CQ
 */
public abstract class AbstractAdviceFactory {
    private final Map<Class<? extends Advice>, Advice> instanceCache = new ConcurrentHashMap<>();

    /**
     * 根据类型获取Advice实例
     *
     * @param adviceClass
     * @return
     */
    protected abstract Advice getAdviceInstance(Class<? extends Advice> adviceClass);


    /**
     * 根据类型获取Advice实例
     */
    public final Advice get(Class<? extends Advice> adviceClass) {
        Advice advice = instanceCache.get(adviceClass);
        if (null != advice) {
            return advice;
        }
        advice = getAdviceInstance(adviceClass);
        if (null == advice) {
            throw new AdviceRuntimeException(adviceClass + " advice instance is null");
        }
        advice = proxy(advice);
        if (!adviceClass.isAnnotationPresent(DisableCache.class)) {
            instanceCache.put(adviceClass, advice);
        }
        return advice;
    }


    /**
     * 代理对象
     */
    private Advice proxy(Advice advice) {
        if (Proxy.isProxyClass(advice.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(advice);
            if (handler instanceof AdviceProxy) {
                return advice;
            }
        }
        return (Advice) Proxy.newProxyInstance(Advice.class.getClassLoader(), new Class<?>[]{Advice.class}, new AdviceProxy(advice));
    }


    /**
     * 是否有效的实现类
     */
    protected final boolean isEffectiveAdviceClass(Class<? extends Advice> cls) {
        int modifiers = cls.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers);
    }

}
