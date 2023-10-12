package xyz.fsgik.common.convert.handlers;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.base.Fs;
import xyz.fsgik.common.convert.FsConverter;
import xyz.fsgik.common.io.FsBuffer;
import xyz.fsgik.common.reflect.FsType;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Convert handler implementation which is used to support the conversion between bytes types.
 * It supports types:
 * <ul>
 *     <li>byte[];</li>
 *     <li>{@link ByteBuffer};</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link Fs#CONTINUE}.
 *
 * @author fredsuvn
 */
public class BytesConvertHandler implements FsConverter.Handler {

    /**
     * An instance.
     */
    public static final BytesConvertHandler INSTANCE = new BytesConvertHandler();

    @Override
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
        if (source == null) {
            return Fs.CONTINUE;
        }
        if (Objects.equals(targetType, byte[].class)) {
            if (Objects.equals(sourceType, byte[].class)) {
                byte[] src = (byte[]) source;
                if (converter.getOptions().reusePolicy() == FsConverter.Options.NO_REUSE) {
                    return src.clone();
                }
                return source;
            } else if (FsType.isAssignableFrom(ByteBuffer.class, sourceType)) {
                ByteBuffer src = ((ByteBuffer) source).slice();
                return FsBuffer.getBytes(src);
            } else {
                return Fs.CONTINUE;
            }
        } else if (Objects.equals(targetType, ByteBuffer.class)) {
            if (Objects.equals(sourceType, byte[].class)) {
                byte[] src = (byte[]) source;
                if (converter.getOptions().reusePolicy() == FsConverter.Options.NO_REUSE) {
                    return ByteBuffer.wrap(src.clone());
                }
                return ByteBuffer.wrap(src);
            } else if (FsType.isAssignableFrom(ByteBuffer.class, sourceType)) {
                ByteBuffer src = (ByteBuffer) source;
                ByteBuffer slice = src.slice();
                ByteBuffer buffer = ByteBuffer.allocate(slice.remaining());
                buffer.put(slice);
                buffer.flip();
                return buffer;
            } else {
                return Fs.CONTINUE;
            }
        } else {
            return Fs.CONTINUE;
        }
    }
}
