package xyz.fsgek.common.data.protobuf;

import com.google.protobuf.Message;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.bean.GekBeanResolver;
import xyz.fsgek.common.convert.GekConvertException;
import xyz.fsgek.common.convert.GekConverter;
import xyz.fsgek.common.convert.handlers.BeanConvertHandler;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Convert handler implementation for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * This class extends {@link BeanConvertHandler} to supports convert to protobuf types.
 *
 * @author fredsuvn
 */
public class ProtobufBeanConvertHandler extends BeanConvertHandler {

    /**
     * An instance with {@link #ProtobufBeanConvertHandler()}.
     */
    public static final ProtobufBeanConvertHandler INSTANCE = new ProtobufBeanConvertHandler();

    /**
     * Constructs with {@link GekProtobuf#protobufBeanCopier()}.
     *
     * @see #ProtobufBeanConvertHandler(GekBeanResolver)
     */
    public ProtobufBeanConvertHandler() {
        this(GekProtobuf.protobufBeanResolver());
    }

    /**
     * Constructs with given object converter.
     *
     * @param resolver given object converter
     */
    public ProtobufBeanConvertHandler(GekBeanResolver resolver) {
        super(resolver);
    }

    @Override
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, GekConverter converter) {
        if (source == null) {
            return null;
        }
        if (!(targetType instanceof Class<?>)) {
            return super.convert(source, sourceType, targetType, converter);
        }
        Class<?> rawType = GekReflect.getRawType(targetType);
        //Check whether it is a protobuf object
        boolean isProtobuf = false;
        boolean isBuilder = false;
        if (Message.class.isAssignableFrom(rawType)) {
            isProtobuf = true;
        }
        if (Message.Builder.class.isAssignableFrom(rawType)) {
            isProtobuf = true;
            isBuilder = true;
        }
        if (!isProtobuf) {
            return super.convert(source, sourceType, targetType, converter);
        }
        try {
            Object builder = getProtobufBuilder(rawType, isBuilder);
            getCopier().withConverter(converter)

                .copyProperties(source, sourceType, builder, builder.getClass());
            return build(builder, isBuilder);
        } catch (GekConvertException e) {
            throw e;
        } catch (Exception e) {
            throw new GekConvertException(e);
        }
    }

    @Nullable
    private Object getProtobufBuilder(Class<?> type, boolean isBuilder) throws Exception {
        if (isBuilder) {
            Method build = type.getMethod("build");
            Class<?> srcType = build.getReturnType();
            Method newBuilder = srcType.getMethod("newBuilder");
            return newBuilder.invoke(null);
        } else {
            Method newBuilder = type.getMethod("newBuilder");
            return newBuilder.invoke(null);
        }
    }

    private Object build(Object builder, boolean isBuilder) throws Exception {
        if (isBuilder) {
            return builder;
        }
        Method build = builder.getClass().getMethod("build");
        return build.invoke(builder);
    }
}
