package xyz.fslabo.common.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * This interface represents a bytes transmission that transmit bytes from a data source into a destination. There are
 * two types of method in this interface:
 * <ul>
 *     <li>
 *         The setting methods, used to set this transmission before this it is started;
 *     </li>
 *     <li>
 *         Final methods, start transmission, and when the transmission is finished, the current instance will
 *         be invalid
 *     </li>
 * </ul>
 * If the size of source or destination is unknown, the transmission will keep sending data as long as possible, until
 * either all the data is transmitted or a written exception occurs. Otherwise, the transmission will in bounds of
 * remaining size.
 *
 * @author fredsuvn
 */
public interface Transmission {

    /**
     * Sets the data source to read.
     * <p>
     * This is a setting method.
     *
     * @param source the data source to read
     * @return this
     */
    Transmission source(InputStream source);

    /**
     * Sets the data source to read.
     * <p>
     * This is a setting method.
     *
     * @param source the data source to read
     * @return this
     */
    Transmission source(byte[] source);

    /**
     * Sets the data source to read, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param source the data source to read
     * @param offset start index
     * @param length specified length
     * @return this
     */
    Transmission source(byte[] source, int offset, int length);

    /**
     * Sets the data source to read.
     * <p>
     * This is a setting method.
     *
     * @param source the data source to read
     * @return this
     */
    Transmission source(ByteBuffer source);

    /**
     * Sets the destination to written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to written
     * @return this
     */
    Transmission dest(OutputStream dest);

    /**
     * Sets the destination to written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to written
     * @return this
     */
    Transmission dest(byte[] dest);

    /**
     * Sets the destination to written, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param dest   the destination to written
     * @param offset start index
     * @param length specified length
     * @return this
     */
    Transmission dest(byte[] dest, int offset, int length);

    /**
     * Sets the destination to written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to written
     * @return this
     */
    Transmission dest(ByteBuffer dest);

    /**
     * Sets the bytes number to read. May be -1 if set to read to end, and this is default setting.
     * <p>
     * This is a setting method.
     *
     * @param readSize the bytes number to read
     * @return this
     */
    Transmission readSize(long readSize);

    /**
     * Sets the bytes number for each reading from data source. This setting is used for read stream or need data
     * conversion, default is {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is a setting method.
     *
     * @param blockSize the bytes number for each reading from data source
     * @return this
     */
    Transmission blockSize(int blockSize);

    /**
     * Sets whether break transmission immediately when the number of bytes read is 0. If it is set to {@code false},
     * the reading will continue until reach to end of the source. Default is {@code false}.
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
     * {@code readSize} is less than {@code blockSize}, only bytes of {@code readSize} will be read and passed into the
     * conversion). Default is {@code null}.
     * <p>
     * Note that the {@link ByteBuffer} instance passed as the argument may not always be new, it could be reused. And
     * returned {@link ByteBuffer} will also be considered as such.
     * <p>
     * This is a setting method.
     *
     * @param conversion data conversion
     * @return this
     */
    Transmission conversion(Function<ByteBuffer, ByteBuffer> conversion);

    /**
     * Starts this operation, returns actual written bytes number which is success to written into the destination.
     * Specifically, if it is detected that the data source reaches to the end and no data has been written, return -1.
     * <p>
     * This is a final method.
     *
     * @return actual written bytes number which is success to written into the destination
     * @throws IORuntimeException IO runtime exception
     */
    long start() throws IORuntimeException;
}
