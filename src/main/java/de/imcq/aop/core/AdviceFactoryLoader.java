package de.imcq.aop.core;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 加载AdviceFactory
 *
 * @author CQ
 * @see AbstractAdviceFactory
 */
public final class AdviceFactoryLoader {
    private final AbstractAdviceFactory factory;

    public static AbstractAdviceFactory load() {
        return Instance.ADVICE_FACTORY_LOADER.factory;
    }

    private AdviceFactoryLoader() {
        //通过SPI加载AbstractAdviceFactory的实现类，多个取第一个，没有使用默认
        ServiceLoader<AbstractAdviceFactory> factories = ServiceLoader.load(AbstractAdviceFactory.class);
        Iterator<AbstractAdviceFactory> iterator = factories.iterator();
        factory = iterator.hasNext() ? iterator.next() : new DefaultAdviceFactory();
        System.err.println("Load AdviceFactory by " + factory.getClass().getName());
    }

    private static class Instance {
        private static final AdviceFactoryLoader ADVICE_FACTORY_LOADER = new AdviceFactoryLoader();
    }
}
