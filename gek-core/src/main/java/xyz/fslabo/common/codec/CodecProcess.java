package xyz.fslabo.common.codec;

import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.GekChars;
import xyz.fslabo.common.base.GekString;
import xyz.fslabo.common.io.GekIOProcess;

/**
 * This interface represents a {@link GekIOProcess} of codec process,
 * supports configure and do final in method chaining.
 * Generally its instance is reusable, re-set and re-codec are permitted.
 * See {@link CipherCodec}, {@link Base64Codec} and {@link HexCodec}.
 *
 * @param <T> subtype
 * @author fredsuvn
 * @see CipherCodec
 * @see Base64Codec
 * @see HexCodec
 */
public interface CodecProcess<T extends CodecProcess<T>> extends GekIOProcess<T> {

    /**
     * Sets input to given string with {@link GekChars#latinCharset()}.
     *
     * @param str given string
     * @return this
     */
    default T inputLatin(String str) {
        return Jie.as(input(GekString.encode(str, GekChars.latinCharset())));
    }

    /**
     * Starts and does final process, builds result as string with {@link GekChars#latinCharset()} and returns.
     *
     * @return result as string with ISO_8859_1 charset
     */
    default String finalLatinString() {
        return finalString(GekChars.latinCharset());
    }
}
