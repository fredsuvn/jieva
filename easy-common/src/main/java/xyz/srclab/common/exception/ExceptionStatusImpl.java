package xyz.srclab.common.exception;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.state.StateHelper;
import xyz.srclab.common.state.StringStateHelper;

/**
 * @author sunqian
 */
class ExceptionStatusImpl implements ExceptionStatus {

    private final String code;
    private final @Nullable String descriptor;
    private final String toString;

    ExceptionStatusImpl(String code, @Nullable String descriptor) {
        this.code = code;
        this.descriptor = descriptor;
        this.toString = StateHelper.toString(this);
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public @Nullable String getDescription() {
        return descriptor;
    }

    @Override
    public ExceptionStatus withMoreDescription(String moreDescription) {
        return new ExceptionStatusImpl(code, StringStateHelper.buildDescription(descriptor, moreDescription));
    }

    @Override
    public String toString() {
        return toString;
    }

    @Override
    public boolean equals(Object other) {
        return StateHelper.equals(this, other);
    }

    @Override
    public int hashCode() {
        return StateHelper.hashCode(this);
    }
}
