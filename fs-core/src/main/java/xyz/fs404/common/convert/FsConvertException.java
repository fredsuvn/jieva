package xyz.fs404.common.convert;

import java.lang.reflect.Type;

/**
 * Exception for conversion.
 *
 * @author fredsuvn
 */
public class FsConvertException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsConvertException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsConvertException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsConvertException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with source type and target type.
     *
     * @param sourceType source type
     * @param targetType target type
     */
    public FsConvertException(Type sourceType, Type targetType) {
        this("Failed to convert from " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".");
    }

    /**
     * Constructs with source type, target type and exception cause.
     *
     * @param sourceType source type
     * @param targetType target type
     * @param cause      exception cause
     */
    public FsConvertException(Type sourceType, Type targetType, Throwable cause) {
        this("Failed to convert from " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".", cause);
    }
}
