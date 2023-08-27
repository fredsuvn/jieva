package xyz.srclab.common.codec;

/**
 * Exception for codec.
 *
 * @author fredsuvn
 */
public class FsCodecException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsCodecException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsCodecException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsCodecException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsCodecException(Throwable cause) {
        super(cause);
    }
}
