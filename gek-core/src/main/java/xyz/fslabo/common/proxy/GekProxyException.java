package xyz.fslabo.common.proxy;

/**
 * Exception for Jie proxy.
 *
 * @author fredsuvn
 */
public class GekProxyException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekProxyException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekProxyException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekProxyException(Throwable cause) {
        super(cause);
    }
}
