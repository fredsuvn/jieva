package xyz.fsgek.common.data;

import java.lang.reflect.Type;

/**
 * Exception for data object copy.
 *
 * @author fredsuvn
 */
public class GekDataCopyException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekDataCopyException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekDataCopyException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekDataCopyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekDataCopyException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with source type and destination type.
     *
     * @param sourceType source type
     * @param destType   destination type
     */
    public GekDataCopyException(Type sourceType, Type destType) {
        this("Failed to copy properties from " + sourceType.getTypeName() + " to " + destType.getTypeName() + ".");
    }

    /**
     * Constructs with source type, destination type and exception cause.
     *
     * @param sourceType source type
     * @param destType   destination type
     * @param cause      exception cause
     */
    public GekDataCopyException(Type sourceType, Type destType, Throwable cause) {
        this("Failed to copy properties from " + sourceType.getTypeName() + " to " + destType.getTypeName() + ".", cause);
    }
}
