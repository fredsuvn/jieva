package xyz.fsgek.common.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * This interface represents base interface of data processor, it allows setting the input which is to be processed and
 * output which is destination of precessing, and finally executing the process method to perform the processing with
 * {@link #process()}. It uses chain-calling to set input/output and finally call {@link #process()} to do process:
 * <pre>
 *     processor
 *      .input(in)
 *      .output(out)
 *      .process();
 * </pre>
 * Subtype needs to play the role of {@link T}, which represents subtype itself:
 * <pre>
 *     class SubProcessor implements GekDataProcessor&lt;SubProcessor&gt;
 * </pre>
 *
 * @param <T> subtype of this interface, and is subtype itself
 * @author fredsuvn
 */
public interface GekDataProcessor<T extends GekDataProcessor<T>> extends GekDataInput<T>{

    /**
     * Sets output to given array.
     *
     * @param array given array
     * @return this
     */
    default T output(byte[] array) {
        return output(array, 0);
    }

    /**
     * Sets output to given array, starting from given offset.
     *
     * @param array  given array
     * @param offset given offset
     * @return this
     */
    default T output(byte[] array, int offset) {
        return output(ByteBuffer.wrap(array, offset, array.length - offset));
    }

    /**
     * Sets output to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    T output(ByteBuffer buffer);

    /**
     * Sets output to given output stream.
     *
     * @param out given output stream
     * @return this
     */
    T output(OutputStream out);

    /**
     * Does the data-processing.
     *
     * @return the number of bytes stored in output
     */
    long process();
}
