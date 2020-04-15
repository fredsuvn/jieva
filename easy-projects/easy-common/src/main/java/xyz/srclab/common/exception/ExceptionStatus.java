package xyz.srclab.common.exception;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.state.StringState;
import xyz.srclab.common.state.StringStateHelper;

@Immutable
public interface ExceptionStatus extends StringState<ExceptionStatus> {

    static ExceptionStatus newExceptionStatus(String code) {
        return newExceptionStatus(code, null);
    }

    static ExceptionStatus newExceptionStatus(String code, @Nullable String description) {
        return new ExceptionStatusImpl(code, description);
    }

    static ExceptionStatus from(ExceptionStatus exceptionStatus) {
        return newExceptionStatus(exceptionStatus.getCode(), exceptionStatus.getDescription());
    }

    @Override
    default ExceptionStatus withMoreDescription(String moreDescription) {
        return new ExceptionStatusImpl(
                getCode(), StringStateHelper.buildDescription(getDescription(), moreDescription));
    }
}
