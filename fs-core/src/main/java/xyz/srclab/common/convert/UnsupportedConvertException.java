package xyz.srclab.common.convert;

import java.lang.reflect.Type;

/**
 * Represents the converting is unsupported.
 *
 * @author fredsuvn
 */
public class UnsupportedConvertException extends RuntimeException {

    public UnsupportedConvertException() {
    }

    public UnsupportedConvertException(String message) {
        super(message);
    }

    public UnsupportedConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedConvertException(Throwable cause) {
        super(cause);
    }

    public UnsupportedConvertException(Type fromType, Type targetType) {
        this("Unsupported convert from " + fromType.getTypeName() + " to " + targetType.getTypeName() + ".");
    }
}
