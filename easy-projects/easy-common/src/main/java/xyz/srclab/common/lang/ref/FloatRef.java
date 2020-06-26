package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface FloatRef {

    static FloatRef empty() {
        return RefSupport.newFloatRef();
    }

    static FloatRef of(float value) {
        return RefSupport.newFloatRef(value);
    }

    float get();

    void set(float value);
}
