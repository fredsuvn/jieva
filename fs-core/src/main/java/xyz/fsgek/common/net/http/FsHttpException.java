package xyz.fsgek.common.net.http;

/**
 * Exception for http.
 *
 * @author fredsuvn
 */
public class FsHttpException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsHttpException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsHttpException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsHttpException(Throwable cause) {
        super(cause);
    }
}
