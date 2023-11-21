package xyz.fsgek.common.codec;

import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.GekChars;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.io.GekIOProcess;

/**
 * {@link GekIOProcess} for codec operation.
 *
 * @param <T> subtype
 * @author fredsuvn
 */
public interface CodecProcess<T extends CodecProcess<T>> extends GekIOProcess<T> {

    /**
     * Sets input to given string with {@link GekChars#ISO_8859_1}.
     *
     * @param str given string
     * @return this
     */
    default T inputLatin(String str) {
        return Gek.as(input(GekString.encode(str, GekChars.ISO_8859_1)));
    }

    /**
     * Starts and does final process, builds result as string with {@link GekChars#ISO_8859_1} and returns.
     *
     * @return result as string with ISO_8859_1 charset
     */
    default String finalLatinString() {
        return finalString(GekChars.ISO_8859_1);
    }
}
