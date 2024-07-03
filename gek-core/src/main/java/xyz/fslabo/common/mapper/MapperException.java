package xyz.fslabo.common.mapper;

import java.lang.reflect.Type;

/**
 * Exception for mapper.
 *
 * @author fredsuvn
 */
public class MapperException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public MapperException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public MapperException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public MapperException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with source type and target type.
     *
     * @param sourceType source type
     * @param targetType target type
     */
    public MapperException(Type sourceType, Type targetType) {
        this("Failed to map " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".");
    }

    /**
     * Constructs with source type, target type and exception cause.
     *
     * @param sourceType source type
     * @param targetType target type
     * @param cause      exception cause
     */
    public MapperException(Type sourceType, Type targetType, Throwable cause) {
        this("Failed to map " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".", cause);
    }
}
