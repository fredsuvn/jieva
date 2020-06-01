package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface BooleanRef {

    static BooleanRef empty() {
        return Ref0.newBooleanRef();
    }

    static BooleanRef of(boolean value) {
        return Ref0.newBooleanRef(value);
    }

    boolean get();

    void set(boolean value);
}
