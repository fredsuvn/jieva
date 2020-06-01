package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface FloatRef {

    static FloatRef empty() {
        return Ref0.newFloatRef();
    }

    static FloatRef of(float value) {
        return Ref0.newFloatRef(value);
    }

    float get();

    void set(float value);
}
