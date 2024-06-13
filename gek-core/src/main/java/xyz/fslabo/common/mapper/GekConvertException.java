package xyz.fslabo.common.mapper;

import java.lang.reflect.Type;

/**
 * Exception for conversion.
 *
 * @author fredsuvn
 */
public class GekConvertException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekConvertException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekConvertException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekConvertException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with source type and target type.
     *
     * @param sourceType source type
     * @param targetType target type
     */
    public GekConvertException(Type sourceType, Type targetType) {
        this("Failed to convert from " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".");
    }

    /**
     * Constructs with source type, target type and exception cause.
     *
     * @param sourceType source type
     * @param targetType target type
     * @param cause      exception cause
     */
    public GekConvertException(Type sourceType, Type targetType, Throwable cause) {
        this("Failed to convert from " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".", cause);
    }
}
