package xyz.fslabo.common.io;

import xyz.fslabo.common.base.GekChars;
import xyz.fslabo.common.base.GekConfigurer;
import xyz.fslabo.common.base.GekString;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * This is base interface for IO version of {@link GekConfigurer}, provides input/output methods configurations:
 * <pre>
 *     configurer.input(in).output(out);
 * </pre>
 * Subtype needs to play the role of {@link T}, which represents subtype itself. For example, here is a subtype:
 * <pre>
 *     interface MyProcess extends GekIOConfigurer&lt;MyProcess&gt; {
 *
 *         void start();
 *     }
 * </pre>
 * Then this subtype can start its process in method chaining:
 * <pre>
 *     myProcess.input(in).output(out).start();
 * </pre>
 *
 * @param <T> subtype of this interface, and is subtype itself
 * @author fredsuvn
 */
public interface GekIOConfigurer<T extends GekIOConfigurer<T>> extends GekConfigurer<T> {

    /**
     * Sets input to given array.
     *
     * @param array given array
     * @return this
     */
    T input(byte[] array);

    /**
     * Sets input to given array, starting from given offset to end of array.
     *
     * @param array  given array
     * @param offset given offset
     * @return this
     */
    default T input(byte[] array, int offset) {
        return input(array, offset, array.length);
    }

    /**
     * Sets input to given array, starting from given offset to specified length.
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

    /**
     * Sets input to given string with {@link GekChars#defaultCharset()}.
     *
     * @param str given string
     * @return this
     */
    default T input(String str) {
        return input(str, GekChars.defaultCharset());
    }

    /**
     * Sets input to given string with specified charset.
     *
     * @param str     given string
     * @param charset specified charset
     * @return this
     */
    default T input(String str, Charset charset) {
        return input(GekString.encode(str, charset));
    }

    /**
     * Sets output to given array.
     *
     * @param array given array
     * @return this
     */
    T output(byte[] array);

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
}
