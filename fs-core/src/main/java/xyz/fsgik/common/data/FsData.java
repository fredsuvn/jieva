package xyz.fsgik.common.data;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;
import xyz.fsgik.common.base.FsChars;
import xyz.fsgik.common.base.FsCheck;
import xyz.fsgik.common.base.FsString;
import xyz.fsgik.common.encode.FsEncoder;
import xyz.fsgik.common.io.FsIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Supplier;

/**
 * This interface represents data adapter to convert or write data into other types.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface FsData {

    /**
     * Wraps given string to {@link FsData}.
     * The given string will be decoded by {@link FsChars#defaultCharset()}.
     *
     * @param str given string
     * @return wrapped {@link FsData}
     */
    static FsData wrap(String str) {
        return wrap(FsString.encode(str));
    }

    /**
     * Wraps given array to {@link FsData}.
     * The given array will be back array of returned data.
     *
     * @param array given array
     * @return wrapped {@link FsData}
     */
    static FsData wrap(byte[] array) {
        return wrap(array, 0, array.length);
    }

    /**
     * Wraps given array of specified length to {@link FsData}, start from offset index.
     * The given array will be back array of returned data.
     *
     * @param array  given array
     * @param offset specified length
     * @param length start offset index
     * @return wrapped {@link FsData}
     */
    static FsData wrap(byte[] array, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, array.length);
        return new ArrayData(array, offset, length);
    }

    /**
     * Wraps given buffer to {@link FsData}.
     *
     * @param buffer given buffer
     * @return wrapped {@link FsData}
     */
    static FsData wrap(ByteBuffer buffer) {
        return new BufferData(buffer);
    }

    /**
     * Returns an instance of {@link FsData} of which data comes from given input stream.
     * <p>
     * Note the state of stream reflects methods of returned data.
     *
     * @param inputStream given input stream
     * @return an instance of {@link FsData} of which data comes from given input stream
     */
    static FsData from(InputStream inputStream) {
        return new InputStreamData(inputStream);
    }

    /**
     * Returns an instance of {@link FsData} of which data comes from given supplier.
     * The returned data will call {@link Supplier#get()} for each method calling of {@link FsData}.
     *
     * @param supplier given supplier
     * @return an instance of {@link FsData} of which data comes from given supplier
     */
    static FsData from(Supplier<byte[]> supplier) {
        return new ArraySupplierData(supplier);
    }

    /**
     * Writes the data into a byte array and returns.
     *
     * @return the byte array
     */
    byte[] toBytes();

    /**
     * Writes the data into dest byte array, returns actual written count.
     *
     * @param dest dest byte array
     * @return actual written count
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
     * @return actual written count
     */
    int write(byte[] dest, int offset, int length);

    /**
     * Writes the data into a byte buffer and returns.
     *
     * @return the byte buffer
     */
    default ByteBuffer toBuffer() {
        return ByteBuffer.wrap(toBytes());
    }

    /**
     * Writes the data into dest byte buffer, returns actual written count.
     *
     * @param dest dest byte buffer
     * @return actual written count
     */
    default int write(ByteBuffer dest) {
        return (int) write(FsIO.toOutputStream(dest));
    }

    /**
     * Returns an input stream for reading bytes from this data.
     *
     * @return an input stream for reading bytes from this data
     */
    InputStream toInputStream();

    /**
     * Writes the data into dest output stream, returns actual written count.
     *
     * @param dest dest output stream
     * @return actual written count
     */
    default long write(OutputStream dest) {
        return FsIO.readBytesTo(toInputStream(), dest);
    }

    /**
     * Returns string with specified charset of this data.
     *
     * @param charset specified charset
     * @return string with specified charset of this data
     */
    default String toString(Charset charset) {
        return new String(toBytes(), charset);
    }

    /**
     * Returns base64 string of this data.
     *
     * @return base64 string of this data
     */
    default String toBase64String() {
        return FsEncoder.base64().encodeToString(toBytes());
    }

    /**
     * Returns whether this data is backed by an array.
     *
     * @return whether this data is backed by an array
     */
    boolean hasBackArray();

    /**
     * Returns array which back this data, or null if it hasn't.
     *
     * @return array which back this data, or null if it hasn't
     */
    @Nullable
    byte[] backArray();
}
