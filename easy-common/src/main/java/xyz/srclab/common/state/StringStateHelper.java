package xyz.srclab.common.state;

import xyz.srclab.annotation.Nullable;

public class StringStateHelper {

    public static String buildDescription(@Nullable String description, String moreDescription) {
        return description == null ? moreDescription : description + "[" + moreDescription + "]";
    }

    public static <T extends StringState<T>> String toString(T state) {
        String code = state.getCode();
        @Nullable String description = state.getDescription();
        return description == null ? code : code + ": " + description;
    }
}
