package xyz.fslabo.common.base;

/**
 * Enum of common flag objects.
 *
 * @author fredsuvn
 */
public enum GekFlag {

    /**
     * A flag denotes {@code null}.
     */
    NULL("null"),

    /**
     * A flag denotes concept of {@code continue}, to {@code continue} a loop.
     */
    CONTINUE("continue"),

    /**
     * A flag denotes concept of {@code break}, to {@code break} from a loop.
     */
    BREAK("break"),

    /**
     * A flag denotes concept of {@code return}, to {@code return} from a process.
     */
    RETURN("return"),

    /**
     * A flag denotes concept of {@code goto}, to {@code go to} a point of process.
     */
    GOTO("goto"),
    ;

    private final String name;

    GekFlag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[flag:" + name + "]";
    }
}
