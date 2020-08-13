package de.imcq.aop.core;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 参数名称
 *
 * @author CQ
 */
public final class ParamNames {
    public static final String RANDOM_PARAM_PREFIX;

    private static AtomicLong GLOBAL_ID = new AtomicLong(0);

    static {
        RANDOM_PARAM_PREFIX = "javaf$advice$" + new Random().nextInt(10) + "$";
    }

    public static String getParamName() {
        return RANDOM_PARAM_PREFIX + GLOBAL_ID.getAndIncrement();
    }

    private ParamNames() {

    }
}
