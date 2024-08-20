package xyz.fslabo.common.base;

import xyz.fslabo.annotations.Nullable;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utilities for char array, {@link CharBuffer}, {@link Charset}, etc.
 *
 * @author fredsuvn
 */
public class JieChars {

    /**
     * Charset: UTF-8.
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Charset: ISO-8859-1.
     */
    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    private volatile static Charset NATIVE_CHARSET = null;

    /**
     * Returns default charset: {@link #UTF_8}.
     *
     * @return default charset: {@link #UTF_8}
     */
    public static Charset defaultCharset() {
        return UTF_8;
    }

    /**
     * Returns latin charset: {@link #ISO_8859_1}.
     *
     * @return latin charset: {@link #ISO_8859_1}
     */
    public static Charset latinCharset() {
        return ISO_8859_1;
    }

    /**
     * Returns current system's charset. Generally is JVM environment 's charset not native charset.
     *
     * @return current system's charset.
     * @see #nativeCharset()
     */
    public static Charset systemCharset() {
        return Charset.defaultCharset();
    }

    /**
     * Returns charset of current native environment. Generally is charset of local native OS, not JVM charset.
     *
     * @return charset of current native environment
     * @see #systemCharset()
     */
    @Nullable
    public static Charset nativeCharset() {
        if (NATIVE_CHARSET != null) {
            return NATIVE_CHARSET;
        }
        Charset nativeChars = nativeCharset0();
        NATIVE_CHARSET = nativeChars;
        return nativeChars;
    }

    /**
     * Returns native charset.
     * This will search following system properties in order:
     * <ul>
     *     <li>native.encoding</li>
     *     <li>sun.jnu.encoding</li>
     *     <li>file.encoding</li>
     * </ul>
     * If still not found, return null.
     */
    @Nullable
    private static Charset nativeCharset0() {
        String encoding = JieSystem.getNativeEncoding();
        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (Exception e) {
                //do nothing
            }
        }
        encoding = System.getProperty("sun.jnu.encoding");
        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (Exception e) {
                //do nothing
            }
        }
        encoding = JieSystem.getFileEncoding();
        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (Exception e) {
                //do nothing
            }
        }
        // not found:
        return null;
    }
}
