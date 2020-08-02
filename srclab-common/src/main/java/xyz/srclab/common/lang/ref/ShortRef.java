package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface ShortRef {

    static ShortRef empty() {
        return RefSupport.newShortRef();
    }

    static ShortRef of(short value) {
        return RefSupport.newShortRef(value);
    }

    short get();

    void set(short value);
}
