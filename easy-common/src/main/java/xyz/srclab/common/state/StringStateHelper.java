package xyz.srclab.common.state;

import org.jetbrains.annotations.Nullable;

public class StringStateHelper {

    public static String buildDescription(@Nullable String description, String moreDescription) {
        return description == null ? moreDescription : description + "[" + moreDescription + "]";
    }

    public static <T extends StringState<T>> String toString(T state) {
        String code = state.getCode();
        String description = state.getDescription();
        return description == null ? code : code + ": " + description;
    }
}
