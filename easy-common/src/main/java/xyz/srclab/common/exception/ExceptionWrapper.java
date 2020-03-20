package xyz.srclab.common.exception;

public class ExceptionWrapper extends RuntimeException {

    public ExceptionWrapper(Throwable wrapped) {
        super(wrapped);
    }

    public Throwable getWrapped() {
        return getCause();
    }
}
