package xyz.fsgik.common.base;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.base.ref.BooleanRef;
import xyz.fsgik.common.base.ref.FsRef;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utilities for char array, {@link CharBuffer}, {@link Charset}, etc.
 *
 * @author fredsuvn
 */
public class FsChars {

    /**
     * Charset: UTF-8.
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Charset: ISO-8859-1.
     */
    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    private volatile static Charset NATIVE_CHARSET = null;
    private final static BooleanRef nativeCharsetFlag = FsRef.ofBoolean(false);

    /**
     * Returns default charset: {@link #UTF_8}.
     *
     * @return default charset: {@link #UTF_8}
     */
    public static Charset defaultCharset() {
        return UTF_8;
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
     * Returns charset of current native environment. Generally is charset of local native OS.
     *
     * @return charset of current native environment
     */
    @Nullable
    public static Charset nativeCharset() {
        if (nativeCharsetFlag.get()) {
            return NATIVE_CHARSET;
        }
        synchronized (nativeCharsetFlag) {
            if (nativeCharsetFlag.get()) {
                return NATIVE_CHARSET;
            }
            NATIVE_CHARSET = nativeCharset0();
            nativeCharsetFlag.set(true);
        }
        return NATIVE_CHARSET;
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
        String encoding = FsSystem.getNativeEncoding();
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
        encoding = FsSystem.getFileEncoding();
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
