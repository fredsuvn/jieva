package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface ByteRef {

    static ByteRef empty() {
        return Ref0.newByteRef();
    }

    static ByteRef of(byte value) {
        return Ref0.newByteRef(value);
    }

    byte get();

    void set(byte value);
}
