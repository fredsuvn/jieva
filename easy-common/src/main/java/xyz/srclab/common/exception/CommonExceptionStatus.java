package xyz.srclab.common.exception;

import xyz.srclab.common.state.StringStateHelper;

public enum CommonExceptionStatus implements ExceptionStatus {

    INTERNAL("000000", "Internal Error"),
    ;

    private final String code;
    private final String description;

    CommonExceptionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return StringStateHelper.toString(this);
    }
}
