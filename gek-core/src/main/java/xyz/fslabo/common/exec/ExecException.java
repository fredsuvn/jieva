package xyz.fslabo.common.exec;

/**
 * Exception for execution.
 *
 * @author fredsuvn
 */
public class ExecException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public ExecException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public ExecException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ExecException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public ExecException(Throwable cause) {
        super(cause);
    }
}
