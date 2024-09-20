package xyz.fslabo.common.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * This interface represents a bytes transmission that reads bytes from a data source into a destination. There are two
 * types of method in this interface:
 * <ul>
 *     <li>
 *         The setting methods, used to set this operation before this operation is started;
 *     </li>
 *     <li>
 *         Final methods, used to start this operation, and when the operation is finished, the current instance will
 *         be invalid
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public interface Transmission {

    /**
     * Sets the data source.
     * <p>
     * This is a setting method.
     *
     * @param source the data source
     * @return this
     */
    Transmission source(InputStream source);

    /**
     * Sets the data source.
     * <p>
     * This is a setting method.
     *
     * @param source the data source
     * @return this
     */
    Transmission source(byte[] source);

    /**
     * Sets the data source, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param source the data source
     * @param offset start index
     * @param length specified length
     * @return this
     */
    Transmission source(byte[] source, int offset, int length);

    /**
     * Sets the data source.
     * <p>
     * This is a setting method.
     *
     * @param source the data source
     * @return this
     */
    Transmission source(ByteBuffer source);

    /**
     * Sets the destination.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination
     * @return this
     */
    Transmission dest(OutputStream dest);

    /**
     * Sets the destination.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination
     * @return this
     */
    Transmission dest(byte[] dest);

    /**
     * Sets the destination, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param dest   the destination
     * @param offset start index
     * @param length specified length
     * @return this
     */
    Transmission dest(byte[] dest, int offset, int length);

    /**
     * Sets the destination.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination
     * @return this
     */
    Transmission dest(ByteBuffer dest);

    /**
     * Sets the limit bytes number to read.
     * <p>
     * May be -1 if no limit, means read all bytes, and this is default setting.
     * <p>
     * This is a setting method.
     *
     * @param readLimit the limit bytes number to read
     * @return this
     */
    Transmission readLimit(long readLimit);

    /**
     * Set whether to stop writing when the remaining writable length of destination reaches the end. It may cause an
     * {@link IORuntimeException} if sets to {@code false} and remaining length is not enough.
     * <p>
     * Default is true.
     * <p>
     * This is a setting method.
     *
     * @param writeLimit whether to stop writing when the remaining writable length of destination reaches the end
     * @return this
     */
    Transmission writeLimit(boolean writeLimit);

    /**
     * Sets the bytes number for each reading from data source.
     * <p>
     * Default is {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is a setting method.
     *
     * @param blockSize the bytes number for each reading from data source
     * @return this
     */
    Transmission blockSize(int blockSize);

    /**
     * Sets whether break reading immediately when the number of bytes read is 0. If it is set to {@code false}, the
     * reading will continue until reach to end of the source.
     * <p>
     * Default is false.
     * <p>
     * This is a setting method.
     *
     * @param breakIfNoRead whether break reading immediately when the number of bytes read is 0
     * @return this
     */
    Transmission breakIfNoRead(boolean breakIfNoRead);

    /**
     * Set data conversion. If the conversion is not null, source bytes will be converted by the conversion at first,
     * then written into the destination. Size of source bytes read each time is set by {@link #blockSize(int)} (Note if
     * {@code readLimit} is less than {@code blockSize}, only bytes of {@code readLimit} will be read and passed into
     * the conversion).
     * <p>
     * Default is null.
     * <p>
     * This is a setting method.
     *
     * @param conversion data conversion
     * @return this
     */
    Transmission conversion(Function<ByteBuffer, ByteBuffer> conversion);

    /**
     * Starts this operation, returns actual bytes number which is success to written into the destination.
     * <p>
     * This is a final method.
     *
     * @return actual bytes number which is success to written into the destination
     * @throws IORuntimeException IO runtime exception
     */
    long start() throws IORuntimeException;
}
