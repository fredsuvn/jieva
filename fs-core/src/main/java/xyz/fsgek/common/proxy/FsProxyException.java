package xyz.fsgek.common.proxy;

/**
 * Exception for Fs proxy.
 *
 * @author fredsuvn
 */
public class FsProxyException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsProxyException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsProxyException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsProxyException(Throwable cause) {
        super(cause);
    }
}
