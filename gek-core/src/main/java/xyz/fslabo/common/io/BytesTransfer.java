package xyz.fslabo.common.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * This interface represents a transfer operation for bytes, which reads bytes from a data source into a destination.
 * There are two types of method in this interface:
 * <ul>
 *     <li>
 *         Setting methods, used to set options of current transfer operation before it is started;
 *     </li>
 *     <li>
 *         Final methods, start transfer, and when the transfer is finished, the current instance will be invalid;
 *     </li>
 * </ul>
 * The transfer will keep reading and writing until the source reaches to the end or specified {@code readLimit} (by
 * {@link #readLimit(long)}). Therefore, the destination must ensure it has sufficient remaining space.
 *
 * @author fredsuvn
 */
public interface BytesTransfer {

    /**
     * Returns a new {@link BytesTransfer} with specified data source.
     *
     * @param source specified data source
     * @return this
     */
    static BytesTransfer from(InputStream source) {
        return new BytesTransferImpl(source);
    }

    /**
     * Returns a new {@link BytesTransfer} with specified data source.
     *
     * @param source specified data source
     * @return this
     */
    static BytesTransfer from(byte[] source) {
        return new BytesTransferImpl(source);
    }

    /**
     * Returns a new {@link BytesTransfer} with specified data source, starting from the start index up to the specified
     * length.
     *
     * @param source specified data source
     * @param offset start index
     * @param length specified length
     * @return this
     */
    static BytesTransfer from(byte[] source, int offset, int length) {
        if (offset == 0 && length == source.length) {
            return from(source);
        }
        try {
            ByteBuffer buffer = ByteBuffer.wrap(source, offset, length);
            return from(buffer);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Returns a new {@link BytesTransfer} with specified data source.
     *
     * @param source specified data source
     * @return this
     */
    static BytesTransfer from(ByteBuffer source) {
        return new BytesTransferImpl(source);
    }

    /**
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    BytesTransfer to(OutputStream dest);

    /**
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    BytesTransfer to(byte[] dest);

    /**
     * Sets the destination to be written, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param dest   the destination to be be written
     * @param offset start index
     * @param length specified length
     * @return this
     */
    BytesTransfer to(byte[] dest, int offset, int length);

    /**
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    BytesTransfer to(ByteBuffer dest);

    /**
     * Sets max bytes number to read. May be -1 if set to read to end, and this is default setting.
     * <p>
     * This is a setting method.
     *
     * @param readLimit max bytes number to read
     * @return this
     */
    BytesTransfer readLimit(long readLimit);

    /**
     * Sets the bytes number for each reading from data source. This setting is used for read stream or the transfer
     * which need data transform ({@link #transformer(Function)}), default is {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is a setting method.
     *
     * @param blockSize the bytes number for each reading from data source
     * @return this
     */
    BytesTransfer blockSize(int blockSize);

    /**
     * Sets whether break the transfer operation immediately if a read operation returns zero bytes. If it is set to
     * {@code false}, the reading will continue until reaches to end of the source. Default is {@code false}.
     * <p>
     * This is a setting method.
     *
     * @param breakOnZeroRead whether break reading immediately when the number of bytes read is 0
     * @return this
     */
    BytesTransfer breakOnZeroRead(boolean breakOnZeroRead);

    /**
     * Set the data transformer to transform the source data before it is written to the destination. The transformed
     * data will then be written to the destination. This setting is optional; if not set, the source data will be
     * written directly to the destination.
     * <p>
     * The transfer will call the data transformer after each data read, passing the read data as the argument. The
     * value returned by the transformer will be the actual data written to the destination. The length of data read in
     * each read operation is specified by {@link #blockSize(int)}, although the remaining data of last read may be
     * smaller than this value.
     * <p>
     * Note that the {@link ByteBuffer} passed as the argument is not always a new instance; it may be reused. And the
     * returned {@link ByteBuffer} should also be treated as potentially reusable.
     * <p>
     * This is a setting method.
     *
     * @param transformer data transformer
     * @return this
     */
    BytesTransfer transformer(Function<ByteBuffer, ByteBuffer> transformer);

    /**
     * Starts this transfer, returns the actual bytes number that read and success to deal with.
     * <p>
     * If the {@code transformer} is {@code null}, read number equals to written number. Otherwise, the written number
     * may not equal to read number, and this method returns actual read number. Specifically, if it is detected that
     * the data source reaches to the end and no data has been read, return -1.
     * <p>
     * This is a final method.
     *
     * @return the actual bytes number that read and success to deal with
     * @throws IORuntimeException IO runtime exception
     */
    long start() throws IORuntimeException;
}
