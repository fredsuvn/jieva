package xyz.fslabo.common.codec;

import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.base.JieString;
import xyz.fslabo.common.io.IOConfigurator;

/**
 * {@link IOConfigurator} for codec operations.
 *
 * @param <T> actual type of this {@code CodecConfigurator}
 * @author fredsuvn
 * @see CipherCodec
 * @see Base64Codec
 * @see HexCodec
 */
public interface CodecConfigurator<T extends CodecConfigurator<T>> extends IOConfigurator<T> {

    /**
     * Sets input to given string with {@link JieChars#latinCharset()}.
     *
     * @param str given string
     * @return this
     */
    default T inputLatin(String str) {
        return Jie.as(input(JieString.encode(str, JieChars.latinCharset())));
    }

    /**
     * Starts and does final process, builds result as string with {@link JieChars#latinCharset()} and returns.
     *
     * @return result as string with ISO_8859_1 charset
     */
    default String finalLatin() {
        return finalString(JieChars.latinCharset());
    }
}
