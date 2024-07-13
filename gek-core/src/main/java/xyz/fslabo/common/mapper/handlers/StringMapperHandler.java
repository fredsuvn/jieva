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
import java.util.Objects;

/**
 * Mapper handler which is used to map from any object to string types.
 * <p>
 * Supported target types:
 * <ul>
 *     <li>{@link String};</li>
 *     <li>{@link CharSequence};</li>
 *     <li>{@link StringBuilder};</li>
 *     <li>{@code char[]};</li>
 *     <li>{@link StringBuffer};</li>
 * </ul>
 * If source type is {@code byte[]} or {@link ByteBuffer}, this handler will use {@link MapperOptions#getCharset()} to
 * get specified charset to map.
 *
 * @author fredsuvn
 */
public class StringMapperHandler implements Mapper.Handler {

    /**
     * An instance.
     */
    public static final StringMapperHandler INSTANCE = new StringMapperHandler();

    @Override
    public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MapperOptions options) {
        if (source instanceof byte[]) {
            return byteArrayToString((byte[]) source, targetType, options);
        }
        if (source instanceof ByteBuffer) {
            return byteBufferToString((ByteBuffer) source, targetType, options);
        }
        return objToString(source, targetType);
    }

    private Object objToString(@Nullable Object source, Type targetType) {
        if (Objects.equals(targetType, String.class) || Objects.equals(targetType, CharSequence.class)) {
            return String.valueOf(source);
        } else if (Objects.equals(targetType, StringBuilder.class)) {
            return new StringBuilder(String.valueOf(source));
        } else if (Objects.equals(targetType, char[].class)) {
            return String.valueOf(source).toCharArray();
        } else if (Objects.equals(targetType, StringBuffer.class)) {
            return new StringBuffer(String.valueOf(source));
        } else {
            return Flag.CONTINUE;
        }
    }

    private Object byteArrayToString(
        @Nullable byte[] source, Type targetType, MapperOptions options) {
        Charset charset = Jie.orDefault(options.getCharset(), JieChars.defaultCharset());
        String srcString = source == null ? String.valueOf((byte[]) null) : new String(source, charset);
        return objToString(srcString, targetType);
    }

    private Object byteBufferToString(
        @Nullable ByteBuffer source, Type targetType, MapperOptions options) {
        Charset charset = Jie.orDefault(options.getCharset(), JieChars.defaultCharset());
        String srcString = source == null ? String.valueOf((byte[]) null) : new String(JieIO.read(source), charset);
        return objToString(srcString, targetType);
    }
}
