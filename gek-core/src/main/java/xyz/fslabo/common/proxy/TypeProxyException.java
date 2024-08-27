package xyz.fslabo.common.proxy;

/**
 * Exception for type proxy.
 *
 * @author fredsuvn
 */
public class TypeProxyException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public TypeProxyException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public TypeProxyException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public TypeProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public TypeProxyException(Throwable cause) {
        super(cause);
    }
}
