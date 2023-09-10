package xyz.srclab.common.bean.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.bean.FsBeanResolver;

/**
 * Protobuf bean resolve handler.
 * This handler depends on libs about <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 *
 * @author fredsuvn
 */
public class ProtobufResolveHandler implements FsBeanResolver.Handler {

    @Override
    public @Nullable Object resolve(FsBeanResolver.BeanBuilder builder) {
        return null;
    }
}
