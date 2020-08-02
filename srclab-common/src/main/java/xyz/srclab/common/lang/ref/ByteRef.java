package xyz.srclab.common.lang.ref;

/**
 * @author sunqian
 */
public interface ByteRef {

    static ByteRef empty() {
        return RefSupport.newByteRef();
    }

    static ByteRef of(byte value) {
        return RefSupport.newByteRef(value);
    }

    byte get();

    void set(byte value);
}
