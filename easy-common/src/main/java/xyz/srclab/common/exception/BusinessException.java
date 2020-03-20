package xyz.srclab.common.exception;

import xyz.srclab.common.state.StringStateHelper;

public class BusinessException extends RuntimeException implements ExceptionStatus {

    private static String buildMessage(ExceptionStatus status) {
        return StringStateHelper.toString(status);
    }

    private final ExceptionStatus status;

    public BusinessException(ExceptionStatus status) {
        this(status, null);
    }

    public BusinessException(ExceptionStatus status, Throwable cause) {
        super(buildMessage(status), cause);
        this.status = status;
    }

    @Override
    public String getCode() {
        return status.getCode();
    }

    @Override
    public String getDescription() {
        return status.getDescription();
    }
}
