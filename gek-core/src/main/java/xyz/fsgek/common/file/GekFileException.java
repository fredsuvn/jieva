package xyz.fsgek.common.file;

/**
 * Exception for file.
 *
 * @author fredsuvn
 */
public class GekFileException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekFileException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekFileException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekFileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekFileException(Throwable cause) {
        super(cause);
    }
}
