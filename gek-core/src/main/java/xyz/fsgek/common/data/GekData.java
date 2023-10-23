package xyz.fsgek.common.data;

import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.GekChars;
import xyz.fsgek.common.base.GekCheck;
import xyz.fsgek.common.base.GekString;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Supplier;

/**
 * This interface represents data adapter to convert or write data into other types.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekData extends GekDataOutput {

    /**
     * Wraps given string to {@link GekData}.
     * The given string will be decoded by {@link GekChars#defaultCharset()}.
     *
     * @param str given string
     * @return wrapped {@link GekData}
     */
    static GekData wrap(String str) {
        return wrap(GekString.encode(str));
    }

    /**
     * Wraps given array to {@link GekData}.
     * The given array will be back array of returned data.
     *
     * @param array given array
     * @return wrapped {@link GekData}
     */
    static GekData wrap(byte[] array) {
        return wrap(array, 0, array.length);
    }

    /**
     * Wraps given array of specified length to {@link GekData}, start from offset index.
     * The given array will be back array of returned data.
     *
     * @param array  given array
     * @param offset specified length
     * @param length start offset index
     * @return wrapped {@link GekData}
     */
    static GekData wrap(byte[] array, int offset, int length) {
        GekCheck.checkRangeInBounds(offset, offset + length, 0, array.length);
        return new ArrayData(array, offset, length);
    }

    /**
     * Wraps given buffer to {@link GekData}.
     *
     * @param buffer given buffer
     * @return wrapped {@link GekData}
     */
    static GekData wrap(ByteBuffer buffer) {
        return new BufferData(buffer);
    }

    /**
     * Returns an instance of {@link GekData} of which data comes from given input stream.
     * <p>
     * Note the state of stream reflects methods of returned data.
     *
     * @param inputStream given input stream
     * @return an instance of {@link GekData} of which data comes from given input stream
     */
    static GekData from(InputStream inputStream) {
        return new InputStreamData(inputStream);
    }

    /**
     * Returns an instance of {@link GekData} of which data comes from given supplier.
     * The returned data will call {@link Supplier#get()} for each method calling of {@link GekData}.
     *
     * @param supplier given supplier
     * @return an instance of {@link GekData} of which data comes from given supplier
     */
    static GekData from(Supplier<byte[]> supplier) {
        return new ArraySupplierData(supplier);
    }

    /**
     * Returns string with specified charset of this data.
     *
     * @param charset specified charset
     * @return string with specified charset of this data
     */
    default String toString(Charset charset) {
        return new String(toArray(), charset);
    }

    /**
     * Returns whether this data is type of {@link OfArray} which has a back array.
     *
     * @return whether this data is type of {@link OfArray} which has a back array
     */
    boolean isArrayData();

    /**
     * Returns this data as {@link OfArray} if this is type of {@link OfArray}.
     * Otherwise, it throws {@link GekDataException}.
     *
     * @return this data as {@link OfArray} if this is type of {@link OfArray}
     */
    OfArray asArrayData();

    /**
     * Returns whether this data is type of {@link OfBuffer} which has a back buffer.
     *
     * @return whether this data is type of {@link OfBuffer} which has a back buffer
     */
    boolean isBufferData();

    /**
     * Returns this data as {@link OfBuffer} if this is type of {@link OfBuffer}.
     * Otherwise, it throws {@link GekDataException}.
     *
     * @return this data as {@link OfBuffer} if this is type of {@link OfBuffer}
     */
    OfBuffer asBufferData();

    /**
     * Returns whether this data is type of {@link OfStream} which has a back stream.
     *
     * @return whether this data is type of {@link OfStream} which has a back stream
     */
    boolean isStreamData();

    /**
     * Returns this data as {@link OfStream} if this is type of {@link OfStream}.
     * Otherwise, it throws {@link GekDataException}.
     *
     * @return this data as {@link OfStream} if this is type of {@link OfStream}
     */
    OfStream asStreamData();

    /**
     * A type of {@link GekData} which has a back array to be wrapped.
     * Any operation to back array will be reflected to this data, and vice versa.
     */
    interface OfArray extends GekData {

        /**
         * Returns back array to be wrapped.
         *
         * @return back array to be wrapped
         */
        byte[] array();

        /**
         * Returns wrapped offset of back array.
         *
         * @return wrapped offset of back array
         */
        int arrayOffset();

        /**
         * Returns length of this data, it is also wrapped length of back array.
         *
         * @return length of this data
         */
        int length();
    }

    /**
     * A type of {@link GekData} which has a back buffer.
     * Any operation to back buffer will be reflected to this data
     * (such as moving of position caused by reading/writing), and vice versa.
     */
    interface OfBuffer extends GekData {
        /**
         * Returns back buffer.
         *
         * @return back buffer
         */
        ByteBuffer buffer();
    }

    /**
     * A type of {@link GekData} which has a back stream.
     * Any operation to back stream will be reflected to this data
     * (such as moving of position caused by reading/writing), and vice versa.
     */
    interface OfStream extends GekData {
        /**
         * Returns back stream.
         *
         * @return back stream
         */
        InputStream stream();
    }
}
