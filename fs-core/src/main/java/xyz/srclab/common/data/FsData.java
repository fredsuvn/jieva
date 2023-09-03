package xyz.srclab.common.data;

import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.encode.FsEncoder;
import xyz.srclab.common.io.FsIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * This interface represents a segment of data which is prepared to write.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface FsData {

    /**
     * Returns an instance of {@link FsData} from given array.
     *
     * @param array given array
     */
    static FsData fromBytes(byte[] array) {
        return fromBytes(array, 0, array.length);
    }

    /**
     * Returns an instance of {@link FsData} from given array of specified length and start offset index.
     *
     * @param array  given array
     * @param offset specified length
     * @param length start offset index
     */
    static FsData fromBytes(byte[] array, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, array.length);
        return new ByteArrayData(array, offset, length);
    }

    /**
     * Returns an instance of {@link FsData} from given buffer.
     *
     * @param buffer given buffer
     */
    static FsData fromBuffer(ByteBuffer buffer) {
        return new ByteBufferData(buffer);
    }

    /**
     * Returns an instance of {@link FsData} from given input stream.
     *
     * @param inputStream given input stream
     */
    static FsData fromStream(InputStream inputStream) {
        return new InputStreamData(inputStream);
    }

    /**
     * Writes the data into a byte array and returns.
     */
    byte[] toBytes();

    /**
     * Writes the data into dest byte array, returns actual written count.
     *
     * @param dest dest byte array
     */
    default int write(byte[] dest) {
        return write(dest, 0, dest.length);
    }

    /**
     * Writes the data into dest byte array of specified length from specified offset, returns actual written count.
     *
     * @param dest   dest byte array
     * @param offset specified offset
     * @param length specified length
     */
    int write(byte[] dest, int offset, int length);

    /**
     * Writes the data into a byte buffer and returns.
     */
    default ByteBuffer toBuffer() {
        return ByteBuffer.wrap(toBytes());
    }

    /**
     * Writes the data into dest byte buffer, returns actual written count.
     *
     * @param dest dest byte buffer
     */
    default int write(ByteBuffer dest) {
        return (int) write(FsIO.toOutputStream(dest));
    }

    /**
     * Returns an input stream for reading bytes from this data.
     */
    InputStream toInputStream();

    /**
     * Writes the data into dest output stream, returns actual written count.
     *
     * @param dest dest output stream
     */
    default long write(OutputStream dest) {
        return FsIO.readBytesTo(toInputStream(), dest);
    }

    /**
     * Returns string with specified charset of this data.
     *
     * @param charset specified charset
     */
    default String toString(Charset charset) {
        return new String(toBytes(), charset);
    }

    /**
     * Returns base64 string of this data.
     */
    default String toBase64String() {
        return FsEncoder.base64().encodeToString(toBytes());
    }
}
