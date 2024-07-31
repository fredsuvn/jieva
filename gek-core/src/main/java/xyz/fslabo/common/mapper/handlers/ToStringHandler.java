package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.io.JieIO;
import xyz.fslabo.common.mapper.Mapper;
import xyz.fslabo.common.mapper.MapperOptions;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Objects;

/**
 * Mapper handler which is used to map from any object to string types.
 * <p>
 * Supported target types:
 * <ul>
 *     <li>{@link String};</li>
 *     <li>{@link CharSequence};</li>
 *     <li>{@link StringBuilder};</li>
 *     <li>{@link StringBuffer};</li>
 *     <li>{@code char[]};</li>
 *     <li>{@code byte[]};</li>
 *     <li>{@link ByteBuffer};</li>
 * </ul>
 * Note 1: This handler supports {@link MapperOptions#getCharset()} to set the charset in mapping between
 * {@code byte[]}/{@link ByteBuffer} and string types.
 * <p>
 * Note 2: This handler supports {@link MapperOptions#getNumberFormat()} to set the format in mapping between
 * number types and string types.
 *
 * @author fredsuvn
 */
public class ToStringHandler implements Mapper.Handler {

    /**
     * An instance.
     */
    public static final ToStringHandler INSTANCE = new ToStringHandler();

    @Override
    public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MapperOptions options) {

    }

    private String toString(Object source, Type targetType, MapperOptions options) {
        if (source instanceof Number) {
            return numberToString((Number) source, targetType, options);
        }
        if (source instanceof byte[]) {
            return byteArrayToString((byte[]) source, targetType, options);
        }
        if (source instanceof ByteBuffer) {
            return byteBufferToString((ByteBuffer) source, targetType, options);
        }
        return objToString(source, targetType, options);
    }

    private String numberToString(Number source, Type targetType, MapperOptions options) {
        NumberFormat numberFormat = options.getNumberFormat();
        if (numberFormat == null) {
            return source.toString();
        }
        return numberFormat.format(source);
    }

    private String dateToString(Object source, Type targetType, MapperOptions options) {
    }

    private String bytesToString(Object source, Type targetType, MapperOptions options) {
    }

    private Object objToString(@Nullable Object source, Type targetType, MapperOptions options) {
        if (Objects.equals(targetType, String.class) || Objects.equals(targetType, CharSequence.class)) {
            return String.valueOf(source);
        } else if (Objects.equals(targetType, StringBuilder.class)) {
            return new StringBuilder(String.valueOf(source));
        } else if (Objects.equals(targetType, StringBuffer.class)) {
            return new StringBuffer(String.valueOf(source));
        } else if (Objects.equals(targetType, char[].class)) {
            return String.valueOf(source).toCharArray();
        } else if (Objects.equals(targetType, byte[].class)) {
            return String.valueOf(source).getBytes(getCharset(options));
        } else if (Objects.equals(targetType, ByteBuffer.class)) {
            return ByteBuffer.wrap(String.valueOf(source).getBytes(getCharset(options)));
        } else {
            return Flag.CONTINUE;
        }
    }

    private Object byteArrayToString(
        @Nullable byte[] source, Type targetType, MapperOptions options) {
        return byteArrayToString0(source, targetType, options, true);
    }

    private Object byteBufferToString(
        @Nullable ByteBuffer source, Type targetType, MapperOptions options) {
        source.mark();
        byte[] bytes = JieIO.read(source);
        source.reset();
        return byteArrayToString0(bytes, targetType, options, false);
    }

    private Object byteArrayToString0(
        @Nullable byte[] source, Type targetType, MapperOptions options, boolean deep) {
        if (Objects.equals(targetType, char[].class)) {
            return new String(source, getCharset(options)).toCharArray();
        } else if (Objects.equals(targetType, byte[].class)) {
            return deep ? source.clone() : source;
        } else if (Objects.equals(targetType, ByteBuffer.class)) {
            return ByteBuffer.wrap(deep ? source.clone() : source);
        } else {
            return objToString(new String(source, getCharset(options)), targetType, options);
        }
    }

//    private Object numberToString(Number source, Type targetType, MapperOptions options) {
//        NumberFormat numberFormat = options.getNumberFormat();
//        if (numberFormat == null) {
//            return objToString(source, targetType, options);
//        }
//        if (Objects.equals(targetType, String.class) || Objects.equals(targetType, CharSequence.class)) {
//            return numberFormat.format(source);
//        } else if (Objects.equals(targetType, StringBuilder.class)) {
//            return new StringBuilder(numberFormat.format(source));
//        } else if (Objects.equals(targetType, StringBuffer.class)) {
//            return new StringBuffer(numberFormat.format(source));
//        } else if (Objects.equals(targetType, char[].class)) {
//            return numberFormat.format(source).toCharArray();
//        } else if (Objects.equals(targetType, byte[].class)) {
//            return numberFormat.format(source).getBytes(getCharset(options));
//        } else if (Objects.equals(targetType, ByteBuffer.class)) {
//            return ByteBuffer.wrap(numberFormat.format(source).getBytes(getCharset(options)));
//        } else {
//            return Flag.CONTINUE;
//        }
//    }

    private Charset getCharset(MapperOptions options) {
        return Jie.orDefault(options.getCharset(), JieChars.defaultCharset());
    }
}
