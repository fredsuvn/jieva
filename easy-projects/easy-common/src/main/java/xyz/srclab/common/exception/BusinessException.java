package xyz.srclab.common.exception;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.state.State;
import xyz.srclab.common.state.StateHelper;

public class BusinessException extends RuntimeException implements ExceptionStatus {

    private final ExceptionStatus status;

    public BusinessException(ExceptionStatus status) {
        this(status, null);
    }

    public BusinessException(ExceptionStatus status, @Nullable Throwable cause) {
        super(status.toString(), cause);
        this.status = status;
    }

    @Override
    public String getCode() {
        return status.getCode();
    }

    @Override
    @Nullable
    public String getDescription() {
        return status.getDescription();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof State) {
            return StateHelper.equals(this, other);
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return StateHelper.hashCode(this);
    }
}
