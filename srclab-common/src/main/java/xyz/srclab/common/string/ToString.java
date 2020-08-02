package xyz.srclab.common.string;

import xyz.srclab.annotation.Nullable;

public interface ToString {

    static ToString defaultToString() {
        return ToStringSupport.defaultToString();
    }

    static ToString defaultHumanReadable() {
        return ToStringSupport.defaultHumanReadable();
    }

    static ToString style(ToStringStyle style) {
        return ToStringSupport.newToString(style);
    }

    String toString(@Nullable Object any);
}
