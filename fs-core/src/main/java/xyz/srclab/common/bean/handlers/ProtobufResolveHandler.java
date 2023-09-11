package xyz.srclab.common.bean.handlers;

import com.google.protobuf.MessageOrBuilder;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.bean.FsBeanResolver;

import java.lang.reflect.Method;

/**
 * Protobuf bean resolve handler.
 * This handler depends on libs about <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 *
 * @author fredsuvn
 */
public class ProtobufResolveHandler implements FsBeanResolver.Handler {

    /**
     * An instance.
     */
    public static final ProtobufResolveHandler INSTANCE = new ProtobufResolveHandler();

    @Override
    public @Nullable Object resolve(FsBeanResolver.BeanBuilder builder) {
        Class<?> rawType = builder.getRawType();
        //Check whether it is a protobuf object
        if (!MessageOrBuilder.class.isAssignableFrom(rawType)) {
            return Fs.CONTINUE;
        }
        // if (!MessageLiteOrBuilder::class.java.isAssignableFrom(rawClass)) {
        //     return
        // }
        return null;
    }
}
