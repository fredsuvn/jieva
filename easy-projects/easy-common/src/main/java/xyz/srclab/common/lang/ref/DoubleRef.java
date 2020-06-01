package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface DoubleRef {

    static DoubleRef empty() {
        return Ref0.newDoubleRef();
    }

    static DoubleRef of(double value) {
        return Ref0.newDoubleRef(value);
    }

    double get();

    void set(double value);
}
