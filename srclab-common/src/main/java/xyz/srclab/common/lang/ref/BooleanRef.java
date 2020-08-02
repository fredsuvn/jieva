package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface BooleanRef {

    static BooleanRef empty() {
        return RefSupport.newBooleanRef();
    }

    static BooleanRef of(boolean value) {
        return RefSupport.newBooleanRef(value);
    }

    boolean get();

    void set(boolean value);
}
