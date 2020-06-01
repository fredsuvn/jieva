package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface ShortRef {

    static ShortRef empty() {
        return Ref0.newShortRef();
    }

    static ShortRef of(short value) {
        return Ref0.newShortRef(value);
    }

    short get();

    void set(short value);
}
