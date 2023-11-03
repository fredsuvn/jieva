//package xyz.fsgek.common.io;
//
//import xyz.fsgek.annotations.Nullable;
//import xyz.fsgek.common.base.GekCheck;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;
//import java.util.function.Function;
//
//public class TransformInputStream extends InputStream {
//
//    private final InputStream source;
//    private final int bufferSize;
//    private final Function<ByteBuffer, ByteBuffer> transformer;
//
//    private ByteBuffer buffer;
//    private boolean end = false;
//
//    public TransformInputStream(InputStream source, int bufferSize, Function<ByteBuffer, ByteBuffer> transformer) {
//        this.source = source;
//        this.bufferSize = bufferSize;
//        this.transformer = transformer;
//    }
//
//    @Override
//    public int read(byte[] b) throws IOException {
//        return read(b, 0, b.length);
//    }
//
//    @Override
//    public int read(byte[] b, int off, int len) throws IOException {
//        GekCheck.checkRangeInBounds(off, off + len, 0, b.length);
//        if (end) {
//            return -1;
//        }
//        int offset = off;
//        int length = len;
//        while (length > 0) {
//            if (buffer == null || !buffer.hasRemaining()) {
//                ByteBuffer sourceBuffer = readSource();
//                if (sourceBuffer == null) {
//                    end = true;
//                    break;
//                }
//                buffer = transformer.apply(sourceBuffer);
//            }
//            int minLen = Math.min(buffer.remaining(), length);
//            buffer.get(b, offset, minLen);
//            length -= minLen;
//            offset += minLen;
//        }
//        int readSize = len - length;
//        if (readSize == 0 && end) {
//            return -1;
//        }
//        return readSize;
//    }
//
//    @Override
//    public int read() throws IOException {
//        if (end) {
//            return -1;
//        }
//        if (buffer == null || !buffer.hasRemaining()) {
//            ByteBuffer sourceBuffer = readSource();
//            if (sourceBuffer == null) {
//                end = true;
//                return -1;
//            }
//            buffer = transformer.apply(sourceBuffer);
//        }
//
//    }
//
//    @Nullable
//    private ByteBuffer readSource() throws IOException {
//        byte[] bytes = new byte[bufferSize];
//        int readSize = source.read(bytes);
//        if (readSize < 0) {
//            return null;
//        }
//        return ByteBuffer.wrap(bytes, 0, readSize);
//    }
//
//    @Override
//    public long skip(long n) throws IOException {
//        return super.skip(n);
//    }
//
//    @Override
//    public int available() throws IOException {
//        return super.available();
//    }
//
//    @Override
//    public void close() throws IOException {
//        super.close();
//    }
//
//    @Override
//    public synchronized void mark(int readlimit) {
//        super.mark(readlimit);
//    }
//
//    @Override
//    public synchronized void reset() throws IOException {
//        super.reset();
//    }
//
//    @Override
//    public boolean markSupported() {
//        return super.markSupported();
//    }
//}
