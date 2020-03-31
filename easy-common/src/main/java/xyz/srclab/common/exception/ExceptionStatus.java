package xyz.srclab.common.exception;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.state.StringState;
import xyz.srclab.common.state.StringStateHelper;

public interface ExceptionStatus extends StringState<ExceptionStatus> {

    @Override
    default ExceptionStatus withMoreDescription(String moreDescription) {
        return new ExceptionStatus() {

            @Override
            public String getCode() {
                return ExceptionStatus.this.getCode();
            }

            @Override
            @Nullable
            public String getDescription() {
                return StringStateHelper.buildDescription(ExceptionStatus.this.getDescription(), moreDescription);
            }

            @Override
            public String toString() {
                return StringStateHelper.toString(this);
            }
        };
    }
}
