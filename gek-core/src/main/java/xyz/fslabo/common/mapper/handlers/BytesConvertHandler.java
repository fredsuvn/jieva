package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.mapper.JieMapper;
import xyz.fslabo.common.io.GekIO;
import xyz.fslabo.common.reflect.JieReflect;

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
 * Note if the {@code obj} is null, return {@code null}.
 *
 * @author fredsuvn
 */
public class BytesConvertHandler implements JieMapper.Handler {

    /**
     * An instance.
     */
    public static final BytesConvertHandler INSTANCE = new BytesConvertHandler();

    @Override
    public @Nullable Object map(@Nullable Object source, Type sourceType, Type targetType, JieMapper mapper) {
        if (source == null) {
            return null;
        }
        if (Objects.equals(targetType, byte[].class)) {
            if (Objects.equals(sourceType, byte[].class)) {
                byte[] src = (byte[]) source;
                if (mapper.getOptions().reusePolicy() == JieMapper.Options.NO_REUSE) {
                    return src.clone();
                }
                return source;
            } else if (JieReflect.isAssignableFrom(ByteBuffer.class, sourceType)) {
                ByteBuffer src = ((ByteBuffer) source).slice();
                return GekIO.read(src);
            } else {
                return null;
            }
        } else if (Objects.equals(targetType, ByteBuffer.class)) {
            if (Objects.equals(sourceType, byte[].class)) {
                byte[] src = (byte[]) source;
                if (mapper.getOptions().reusePolicy() == JieMapper.Options.NO_REUSE) {
                    return ByteBuffer.wrap(src.clone());
                }
                return ByteBuffer.wrap(src);
            } else if (JieReflect.isAssignableFrom(ByteBuffer.class, sourceType)) {
                ByteBuffer src = (ByteBuffer) source;
                ByteBuffer slice = src.slice();
                ByteBuffer buffer = ByteBuffer.allocate(slice.remaining());
                buffer.put(slice);
                buffer.flip();
                return buffer;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
