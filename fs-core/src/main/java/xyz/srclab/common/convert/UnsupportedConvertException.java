package xyz.srclab.common.convert;

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
}
