package xyz.srclab.common.state;

import xyz.srclab.annotations.Nullable;

import java.util.Objects;

public class StateHelper {

    public static String toString(State<?, ?, ?> state) {
        Object code = state.getCode();
        @Nullable Object description = state.getDescription();
        return description == null ? code.toString() : code + ": " + description;
    }

    public static boolean equals(State<?, ?, ?> state, Object other) {
        if (state == other) {
            return true;
        }
        if (!(other instanceof State)) {
            return false;
        }
        State<?, ?, ?> that = (State<?, ?, ?>) other;
        return Objects.equals(state.getCode(), that.getCode())
                &&
                Objects.equals(state.getDescription(), that.getDescription());
    }

    public static int hashCode(State<?, ?, ?> state) {
        return Objects.hash(state.getCode(), state.getDescription());
    }
}
