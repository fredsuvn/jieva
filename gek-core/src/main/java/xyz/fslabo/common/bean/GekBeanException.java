package xyz.fslabo.common.bean;

/**
 * Exception for bean.
 *
 * @author fredsuvn
 */
public class GekBeanException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekBeanException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekBeanException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekBeanException(Throwable cause) {
        super(cause);
    }
}
