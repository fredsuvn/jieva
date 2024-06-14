package xyz.fslabo.common.data.protobuf;

import com.google.protobuf.Message;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.bean.BeanResolver;
import xyz.fslabo.common.mapper.GekConvertException;
import xyz.fslabo.common.mapper.JieMapper;
import xyz.fslabo.common.mapper.handlers.BeanConvertHandler;
import xyz.fslabo.common.reflect.JieReflect;

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
     * @see #ProtobufBeanConvertHandler(BeanResolver)
     */
    public ProtobufBeanConvertHandler() {
        this(GekProtobuf.protobufBeanResolver());
    }

    /**
     * Constructs with given object converter.
     *
     * @param resolver given object converter
     */
    public ProtobufBeanConvertHandler(BeanResolver resolver) {
        super(resolver);
    }

    @Override
    public @Nullable Object map(@Nullable Object source, Type sourceType, Type targetType, JieMapper mapper) {
        if (source == null) {
            return null;
        }
        if (!(targetType instanceof Class<?>)) {
            return super.map(source, sourceType, targetType, mapper);
        }
        Class<?> rawType = JieReflect.getRawType(targetType);
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
            return super.map(source, sourceType, targetType, mapper);
        }
        try {
            Object builder = getProtobufBuilder(rawType, isBuilder);
            getCopier().withConverter(mapper)

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
