package xyz.srclab.common.protobuf;

import com.google.protobuf.ByteString;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Convert handler implementation which is used to support the conversion between bytes, string and {@link ByteString}.
 * It supports types:
 * <ul>
 *     <li>byte[];</li>
 *     <li>{@link ByteBuffer};</li>
 *     <li>char[];</li>
 *     <li>{@link String};</li>
 *     <li>{@link ByteString};</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link Fs#CONTINUE}.
 *
 * @author fredsuvn
 */
public class ByteStringConvertHandler implements FsConverter.Handler {

    /**
     * An instance with {@link #ByteStringConvertHandler()}.
     */
    public static final ByteStringConvertHandler INSTANCE = new ByteStringConvertHandler();

    private final Charset charset;

    /**
     * Constructs with {@link FsString#CHARSET}.
     */
    public ByteStringConvertHandler() {
        this.charset = FsString.CHARSET;
    }

    /**
     * Constructs with given charset.
     *
     * @param charset given charset
     */
    public ByteStringConvertHandler(Charset charset) {
        this.charset = charset;
    }

    @Override
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
        if (source == null) {
            return Fs.CONTINUE;
        }
        if (Objects.equals(targetType, ByteString.class)) {
            if (source instanceof ByteString) {
                return source;
            }
            if (source instanceof byte[]) {
                return ByteString.copyFrom((byte[]) source);
            }
            if (source instanceof ByteBuffer) {
                return ByteString.copyFrom(((ByteBuffer) source).slice());
            }
            if (source instanceof String) {
                return ByteString.copyFrom((String) source, charset);
            }
            if (source instanceof char[]) {
                return ByteString.copyFrom(new String((char[]) source), charset);
            }
        }
        if (source instanceof ByteString) {
            ByteString src = (ByteString) source;
            if (Objects.equals(targetType, String.class) || Objects.equals(targetType, CharSequence.class)) {
                return src.toString(charset);
            } else if (Objects.equals(targetType, char[].class)) {
                return src.toString(charset).toCharArray();
            } else if (Objects.equals(targetType, byte[].class)) {
                return src.toByteArray();
            } else if (Objects.equals(targetType, ByteBuffer.class)) {
                return ByteBuffer.wrap(src.toByteArray());
            }
        }
        return Fs.CONTINUE;
    }
}
