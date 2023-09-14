package xyz.srclab.common.net;

/**
 * Exception for network.
 *
 * @author fredsuvn
 */
public class FsNetException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsNetException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsNetException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsNetException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsNetException(Throwable cause) {
        super(cause);
    }
}
