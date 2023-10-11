//package xyz.fsgik.common.base;
//
//import xyz.fsgik.common.io.FsBuffer;
//
//import java.io.ByteArrayOutputStream;
//import java.nio.ByteBuffer;
//
///**
// * Builder for building bytes, just like {@link StringBuilder} but it is used for byte-string.
// *
// * @author fredsuvn
// */
//public class FsBytesBuilder {
//
//    private int initCapacity;
//    private ByteArrayOutputStream out;
//
//    public FsBytesBuilder(int initCapacity) {
//        this.initCapacity = initCapacity;
//    }
//
//    public FsBytesBuilder append(byte b) {
//        getOut().write(b);
//        return this;
//    }
//
//    public FsBytesBuilder append(byte[] bytes) {
//        getOut().write(bytes, 0, bytes.length);
//        return this;
//    }
//
//    public FsBytesBuilder append(byte[] bytes, int offset, int length) {
//        getOut().write(bytes, offset, length);
//        return this;
//    }
//
//    public FsBytesBuilder append(ByteBuffer bytes) {
//        if (!bytes.hasRemaining()) {
//            return this;
//        }
//        if (bytes.hasArray()) {
//            getOut().write(bytes.array(), bytes.arrayOffset() + bytes.position(), bytes.remaining());
//            bytes.position(bytes.position() + bytes.remaining());
//            return this;
//        } else {
//            byte[] remaining = FsBuffer.emptyBuffer()
//        }
//    }
//
//    private ByteArrayOutputStream getOut() {
//        if (out == null) {
//            out = initCapacity <= 0 ? new ByteArrayOutputStream() : new ByteArrayOutputStream(initCapacity);
//        }
//        return out;
//    }
//}
