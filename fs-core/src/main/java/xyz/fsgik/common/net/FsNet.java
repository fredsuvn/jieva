package xyz.fsgik.common.net;

import xyz.fsgik.common.io.FsBuffer;
import xyz.fsgik.common.io.FsIO;
import xyz.fsgik.common.reflect.FsInvoker;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Network utilities.
 *
 * @author fredsuvn
 */
public class FsNet {

    /**
     * Reads and split given buffer in fixed length. This method will move buffer's position by reading.
     * If the remaining length of buffer is not enough to split,
     * the buffer's position will be reset to last start position.
     *
     * @param buffer given buffer
     * @param length fixed length
     */
    public static List<ByteBuffer> splitWithFixedLength(ByteBuffer buffer, int length) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        if (buffer.remaining() < length) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = null;
        while (buffer.remaining() >= length) {
            byte[] bytes = new byte[length];
            buffer.get(bytes);
            if (result == null) {
                result = new LinkedList<>();
            }
            result.add(ByteBuffer.wrap(bytes).asReadOnlyBuffer());
        }
        return result;
    }

    /**
     * Reads and split given buffer in specified length.
     * <p>
     * The split length is specified at offset ({@code lengthOffset}) of buffer, and the {@code lengthSize}
     * specifies width of {@code lengthOffset} (must in 1, 2, 4).
     * <b>The split length value must &lt;= {@link Integer#MAX_VALUE}.</b>
     * <p>
     * This method will move buffer's position by reading.
     * If the remaining length of buffer is not enough to split,
     * the buffer's position will be reset to last start position.
     *
     * @param buffer       given buffer
     * @param lengthOffset offset of length
     * @param lengthSize   length size must in 1, 2, 4
     */
    public static List<ByteBuffer> splitWithSpecifiedLength(ByteBuffer buffer, int lengthOffset, int lengthSize) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        int minSize = lengthOffset + lengthSize;
        if (buffer.remaining() < minSize) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = null;
        while (true) {
            buffer.mark();
            buffer.position(buffer.position() + lengthOffset);
            int length = readLength(buffer, lengthSize);
            buffer.reset();
            if (buffer.remaining() < length) {
                break;
            }
            byte[] bytes = new byte[length];
            buffer.get(bytes);
            if (result == null) {
                result = new LinkedList<>();
            }
            result.add(ByteBuffer.wrap(bytes).asReadOnlyBuffer());
            if (buffer.remaining() < minSize) {
                break;
            }
        }
        return result == null ? Collections.emptyList() : result;
    }

    private static int readLength(ByteBuffer buffer, int lengthSize) {
        switch (lengthSize) {
            case 1:
                return buffer.get() & 0x000000ff;
            case 2:
                return buffer.getShort() & 0x0000ffff;
            case 4:
                return buffer.getInt();
        }
        throw new IllegalArgumentException("lengthSize must in (1, 2, 4).");
    }

//    /**
//     * Reads and split given buffer in specified length.
//     */
//    public static List<ByteBuffer> splitWithDelimiter(ByteBuffer buffer, byte delimiter, byte escape) {
//        if (!buffer.hasRemaining()) {
//            return Collections.emptyList();
//        }
//        List<ByteBuffer> result = new LinkedList<>();
//        while (true) {
//            buffer.mark();
//            while (true) {
//                byte b = buffer.get();
//                if (b == delimiter) {
//                    int pos = buffer.position();
//                    buffer.reset();
//                    if (buffer.position() == pos) {
//                        result.add(FsBuffer.emptyBuffer());
//                    } else {
//
//                    }
//                }
//            }
//        }
//        return result.isEmpty() ? Collections.emptyList() : result;
//    }
}
