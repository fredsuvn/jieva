package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface IntRef {

    static IntRef empty() {
        return Ref0.newIntRef();
    }

    static IntRef of(int value) {
        return Ref0.newIntRef(value);
    }

    int get();

    void set(int value);
}
