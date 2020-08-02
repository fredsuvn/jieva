package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface LongRef {

    static LongRef empty() {
        return RefSupport.newLongRef();
    }

    static LongRef of(long value) {
        return RefSupport.newLongRef(value);
    }

    long get();

    void set(long value);
}
