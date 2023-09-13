package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.io.FsIO;
import xyz.srclab.common.reflect.FsType;

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
            } else if (Objects.equals(sourceType, Byte[].class)) {
                Byte[] src = (Byte[]) source;
                byte[] bytes = new byte[src.length];
                for (int i = 0; i < src.length; i++) {
                    bytes[i] = src[i];
                }
                return bytes;
            } else if (FsType.isAssignableFrom(ByteBuffer.class, sourceType)) {
                ByteBuffer src = ((ByteBuffer) source).slice();
                return FsIO.getBytes(src);
            } else {
                return Fs.CONTINUE;
            }
        } else if (Objects.equals(targetType, Byte[].class)) {
            if (Objects.equals(sourceType, byte[].class)) {
                byte[] src = (byte[]) source;
                Byte[] bytes = new Byte[src.length];
                for (int i = 0; i < src.length; i++) {
                    bytes[i] = src[i];
                }
                return bytes;
            } else if (Objects.equals(sourceType, Byte[].class)) {
                Byte[] src = (Byte[]) source;
                if (converter.getOptions().reusePolicy() == FsConverter.Options.NO_REUSE) {
                    return src.clone();
                }
                return source;
            } else if (FsType.isAssignableFrom(ByteBuffer.class, sourceType)) {
                ByteBuffer src = ((ByteBuffer) source).slice();
                byte[] b = FsIO.getBytes(src);
                Byte[] bytes = new Byte[b.length];
                for (int i = 0; i < b.length; i++) {
                    bytes[i] = b[i];
                }
                return bytes;
            } else {
                return Fs.CONTINUE;
            }
        } else if (FsType.isAssignableFrom(ByteBuffer.class, targetType)) {
            if (Objects.equals(sourceType, byte[].class)) {
                byte[] src = (byte[]) source;
                if (converter.getOptions().reusePolicy() == FsConverter.Options.NO_REUSE) {
                    return ByteBuffer.wrap(src.clone());
                }
                return ByteBuffer.wrap(src);
            } else if (Objects.equals(sourceType, Byte[].class)) {
                Byte[] src = (Byte[]) source;
                ByteBuffer buffer = ByteBuffer.allocate(src.length);
                for (Byte aByte : src) {
                    buffer.put(aByte);
                }
                buffer.flip();
                return buffer;
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
