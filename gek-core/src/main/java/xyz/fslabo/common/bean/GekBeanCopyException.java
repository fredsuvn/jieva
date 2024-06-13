package xyz.fslabo.common.bean;

import java.lang.reflect.Type;

/**
 * Exception for bean object copy.
 *
 * @author fredsuvn
 */
public class GekBeanCopyException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekBeanCopyException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekBeanCopyException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekBeanCopyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekBeanCopyException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with source type and destination type.
     *
     * @param sourceType source type
     * @param destType   destination type
     */
    public GekBeanCopyException(Type sourceType, Type destType) {
        this("Failed to copy properties from " + sourceType.getTypeName() + " to " + destType.getTypeName() + ".");
    }

    /**
     * Constructs with source type, destination type and exception cause.
     *
     * @param sourceType source type
     * @param destType   destination type
     * @param cause      exception cause
     */
    public GekBeanCopyException(Type sourceType, Type destType, Throwable cause) {
        this("Failed to copy properties from " + sourceType.getTypeName() + " to " + destType.getTypeName() + ".", cause);
    }
}
