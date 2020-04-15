package xyz.srclab.common.state;

import xyz.srclab.annotations.Nullable;

public class StringStateHelper {

    public static String buildDescription(@Nullable String description, String moreDescription) {
        return description == null ? moreDescription : description + "[" + moreDescription + "]";
    }
}
