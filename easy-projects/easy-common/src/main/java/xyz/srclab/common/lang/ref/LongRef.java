package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface LongRef {

    static LongRef empty() {
        return Ref0.newLongRef();
    }

    static LongRef of(long value) {
        return Ref0.newLongRef(value);
    }

    long get();

    void set(long value);
}
