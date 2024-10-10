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
     * which need data transformer ({@link #transformer(Function)}), default is {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is a setting method.
     *
     * @param blockSize the bytes number for each reading from data source
     * @return this
     */
    BytesTransfer blockSize(int blockSize);

    /**
     * Sets whether break the transfer operation immediately when the number of bytes read is 0. If it is set to
     * {@code false}, the reading will continue until reach to end of the source. Default is {@code false}.
     * <p>
     * This is a setting method.
     *
     * @param breakIfNoRead whether break reading immediately when the number of bytes read is 0
     * @return this
     */
    BytesTransfer breakIfNoRead(boolean breakIfNoRead);

    /**
     * Sets data transformer. If the transformer is not null, source bytes will be converted by the transformer at
     * first, then written into the destination. Size of source bytes read to convert is determined by
     * {@code blockSize}, but it could be less than {@code blockSize} if remaining readable size is not enough. Default
     * is {@code null}.
     * <p>
     * Note that the {@link ByteBuffer} instance passed as the argument may not always be new, it could be reused. And
     * returned {@link ByteBuffer} will also be considered as such.
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
