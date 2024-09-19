package xyz.fslabo.common.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.time.Duration;

/**
 * This interface represents an IO operation that reads bytes from a data source into a destination. There are two types
 * of method in this interface:
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
public interface ReadTo {

    /**
     * Sets the data source.
     * <p>
     * This is a setting method.
     *
     * @param source the data source
     * @return this
     */
    ReadTo source(InputStream source);

    /**
     * Sets the data source.
     * <p>
     * This is a setting method.
     *
     * @param source the data source
     * @return this
     */
    ReadTo source(byte[] source);

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
    ReadTo source(byte[] source, int offset, int length);

    /**
     * Sets the data source.
     * <p>
     * This is a setting method.
     *
     * @param source the data source
     * @return this
     */
    ReadTo source(ByteBuffer source);

    /**
     * Sets the destination.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination
     * @return this
     */
    ReadTo dest(OutputStream dest);

    /**
     * Sets the destination.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination
     * @return this
     */
    ReadTo dest(byte[] dest);

    /**
     * Sets the destination, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param source the destination
     * @param offset start index
     * @param length specified length
     * @return this
     */
    ReadTo dest(byte[] source, int offset, int length);

    /**
     * Sets the destination.
     * <p>
     * This is a setting method.
     *
     * @param source the destination
     * @return this
     */
    ReadTo dest(ByteBuffer source);

    /**
     * Sets the limit bytes number to read.
     * <p>
     * This is a setting method.
     *
     * @param limit the limit bytes number to read
     * @return this
     */
    ReadTo limit(int limit);

    /**
     * Sets the limit bytes number to read.
     * <p>
     * This is a setting method.
     *
     * @param limit the limit bytes number to read
     * @return this
     */
    ReadTo limit(long limit);

    /**
     * Sets the bytes number for each reading from data source.
     * <p>
     * This is a setting method.
     *
     * @param blockSize the bytes number for each reading from data source
     * @return this
     */
    ReadTo blockSize(int blockSize);

    /**
     * Sets whether break reading immediately when the number of bytes read is 0.
     * <p>
     * This is a setting method.
     *
     * @param breakIfNoRead whether break reading immediately when the number of bytes read is 0
     * @return this
     */
    ReadTo breakIfNoRead(boolean breakIfNoRead);

    /**
     * Sets whether break reading immediately when the number of bytes read is 0.
     * <p>
     * This is a setting method.
     *
     * @param breakIfNoRead whether break reading immediately when the number of bytes read is 0
     * @return this
     */
    ReadTo timeout(Duration timeout);

    /**
     * Starts this operation, returns actual bytes number which is success to written into the destination.
     * <p>
     * This is a final method.
     *
     * @return actual bytes number which is success to written into the destination
     */
    long start();
}
