package xyz.srclab.common.exception;

import xyz.srclab.annotation.Nullable;

public enum DefaultExceptionStatus implements ExceptionStatus {

    INTERNAL("000000", "Internal Error"),
    ;

    private final ExceptionStatus impl;

    DefaultExceptionStatus(String code, @Nullable String description) {
        this.impl = new ExceptionStatusImpl(code, description);
    }

    @Override
    public String getCode() {
        return impl.getCode();
    }

    @Override
    @Nullable
    public String getDescription() {
        return impl.getDescription();
    }

    @Override
    public String toString() {
        return impl.toString();
    }
}
