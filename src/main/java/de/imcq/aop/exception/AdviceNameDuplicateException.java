package de.imcq.aop.exception;

/**
 * @author CQ
 */
public class AdviceNameDuplicateException extends AdviceRuntimeException {
    public AdviceNameDuplicateException() {
        super();
    }

    public AdviceNameDuplicateException(String message) {
        super(message);
    }

    public AdviceNameDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdviceNameDuplicateException(Throwable cause) {
        super(cause);
    }

    protected AdviceNameDuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
