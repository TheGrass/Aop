package de.imcq.aop.exception;

/**
 * @author CQ
 */
public class AdviceRuntimeException extends RuntimeException {
    public AdviceRuntimeException() {
        super();
    }

    public AdviceRuntimeException(String message) {
        super(message);
    }

    public AdviceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdviceRuntimeException(Throwable cause) {
        super(cause);
    }

    protected AdviceRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
