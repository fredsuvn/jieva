// package xyz.srclab.common.net;
//
// import org.jetbrains.annotations.NotNull;
// import xyz.srclab.annotations.Nullable;
// import xyz.srclab.common.io.FsIO;
//
// import java.io.IOException;
// import java.io.InputStream;
// import java.nio.ByteBuffer;
//
// final class NetBufferImpl implements FsNetBuffer {
//
//     private static final ByteBuffer EMPTY = ByteBuffer.wrap(new byte[0]);
//
//     private ByteBuffer buffer;
//
//     NetBufferImpl() {
//         this.buffer = EMPTY;
//     }
//
//     NetBufferImpl(ByteBuffer buffer) {
//         this.buffer = buffer;
//     }
//
//     @Override
//     public synchronized int position() {
//         return buffer.position();
//     }
//
//     @Override
//     public synchronized void position(int position) {
//         buffer.position(position);
//     }
//
//     @Override
//     public synchronized int limit() {
//         return buffer.limit();
//     }
//
//     @Override
//     public boolean hasRemaining() {
//         return buffer.hasRemaining();
//     }
//
//     @Override
//     public int remaining() {
//         return 0;
//     }
//
//     @Override
//     public synchronized void get(byte[] dest) {
//         buffer.get(dest);
//     }
//
//     @Override
//     public synchronized void get(byte[] dest, int offset, int length) {
//         buffer.get(dest, offset, length);
//     }
//
//     @Override
//     public void get(ByteBuffer dest) {
//         dest.put(buffer);
//     }
//
//     @Override
//     public synchronized byte getByte() {
//         return buffer.get();
//     }
//
//     @Override
//     public synchronized short getShort() {
//         return buffer.getShort();
//     }
//
//     @Override
//     public synchronized char getChar() {
//         return buffer.getChar();
//     }
//
//     @Override
//     public synchronized int getInt() {
//         return buffer.getInt();
//     }
//
//     @Override
//     public synchronized long getLong() {
//         return buffer.getLong();
//     }
//
//     @Override
//     public synchronized float getFloat() {
//         return buffer.getFloat();
//     }
//
//     @Override
//     public synchronized double getDouble() {
//         return buffer.getDouble();
//     }
//
//     @Override
//     public synchronized void mark() {
//         buffer.mark();
//     }
//
//     @Override
//     public synchronized void reset() {
//         buffer.reset();
//     }
//
//     @Override
//     public synchronized FsNetBuffer slice() {
//         return new NetBufferImpl(buffer.slice());
//     }
//
//     @Override
//     public synchronized FsNetBuffer slice(int limit) {
//         return new NetBufferImpl(FsIO.slice(buffer, limit));
//     }
//
//     @Override
//     public InputStream toInputStream() {
//         return new InputStream() {
//
//             @Override
//             public int read(@NotNull byte[] b, int off, int len) throws IOException {
//                 return super.read(b, off, len);
//             }
//
//             @Override
//             public int read() throws IOException {
//                 if ()
//                 return 0;
//             }
//         };
//     }
//
//     synchronized void compactAndMerge(@Nullable ByteBuffer newBuffer) {
//         if (newBuffer == null) {
//             if (buffer)
//         }
//     }
// }
