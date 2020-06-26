package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface IntRef {

    static IntRef empty() {
        return RefSupport.newIntRef();
    }

    static IntRef of(int value) {
        return RefSupport.newIntRef(value);
    }

    int get();

    void set(int value);
}
