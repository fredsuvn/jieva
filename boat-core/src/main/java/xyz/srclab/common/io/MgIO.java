package xyz.srclab.common.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * IO utilities.
 *
 * @author fredsuvn
 */
public class MgIO {

    /**
     * Returns a {@link Reader} of which content from given chars.
     *
     * @param chars given chars
     * @return a {@link Reader} of which content from given chars
     */
    public static Reader asReader(CharSequence chars) {
        return new IOImpls.OfCharSequence(chars);
    }

    /**
     * Returns a {@link Reader} of which content from given char array.
     *
     * @param chars given char array
     * @return a {@link Reader} of which content from given char array
     */
    public static Reader asReader(char[] chars) {
        return new IOImpls.OfCharArray(chars, offset, length);
    }

    /**
     * Returns a {@link Reader} of which content from given {@link InputStream}.
     *
     * @param input   given {@link InputStream}
     * @param charset charset of {@link Reader}
     * @return a {@link Reader} of which content from given {@link InputStream}
     * @see InputStreamReader
     */
    public static Reader asReader(InputStream input, Charset charset) {
        return new InputStreamReader(input, charset);
    }

    /**
     * Returns a {@link InputStream} of which content from given {@link Reader}.
     *
     * @param reader  given {@link Reader}
     * @param charset charset of {@link Reader}
     * @return an {@link InputStream} of which content from given {@link Reader}
     * @see IOImpls.OfReader
     */
    public static InputStream asInputStream(Reader reader, Charset charset) {
        return new IOImpls.OfReader(reader, charset, 1024);
    }
}
