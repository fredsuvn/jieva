package xyz.fsgek.common.data;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * This interface represents base interface for setting input data source.
 * It allows setting the input which is to be processed in chain-calling:
 * <pre>
 *     processor.input(in).doFinal();
 * </pre>
 * Subtype needs to play the role of {@link T}, which represents subtype itself:
 * <pre>
 *     class SubInput implements GekDataInput&lt;SubInput&gt;
 * </pre>
 *
 * @param <T> subtype of this interface, and is subtype itself
 * @author fredsuvn
 */
public interface GekDataInput<T extends GekDataInput<T>> {

    /**
     * Sets input to given array.
     *
     * @param array given array
     * @return this
     */
    default T input(byte[] array) {
        return input(array, 0);
    }

    /**
     * Sets input to given array, starting from given offset.
     *
     * @param array  given array
     * @param offset given offset
     * @return this
     */
    default T input(byte[] array, int offset) {
        return input(array, offset, array.length);
    }

    /**
     * Sets input to given array of specified length, starting from given offset.
     *
     * @param array  given array
     * @param offset given offset
     * @param length specified length
     * @return this
     */
    default T input(byte[] array, int offset, int length) {
        return input(ByteBuffer.wrap(array, offset, length));
    }

    /**
     * Sets input to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    T input(ByteBuffer buffer);

    /**
     * Sets input to given input stream.
     *
     * @param in given input stream
     * @return this
     */
    T input(InputStream in);
}
