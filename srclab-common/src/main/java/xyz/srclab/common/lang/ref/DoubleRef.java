package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface DoubleRef {

    static DoubleRef empty() {
        return RefSupport.newDoubleRef();
    }

    static DoubleRef of(double value) {
        return RefSupport.newDoubleRef(value);
    }

    double get();

    void set(double value);
}
