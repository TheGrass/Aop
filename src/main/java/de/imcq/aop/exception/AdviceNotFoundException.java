package de.imcq.aop.exception;

/**
 * @author CQ
 */
public class AdviceNotFoundException extends AdviceRuntimeException {
    public AdviceNotFoundException() {
        super();
    }

    public AdviceNotFoundException(String message) {
        super(message);
    }

    public AdviceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdviceNotFoundException(Throwable cause) {
        super(cause);
    }

    protected AdviceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
