package xyz.fsgek.common.data.protobuf;

import xyz.fsgek.common.data.GekDataResolver;
import xyz.fsgek.common.convert.GekConverter;

/**
 * Utilities for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * This class depends on protobuf libs in the runtime.
 */
public class GekProtobuf {

    private static final GekDataResolver RESOLVER = GekDataResolver.defaultResolver()
        .withFirstHandler(ProtobufResolveHandler.INSTANCE);
    private static final GekConverter CONVERTER = GekConverter.defaultConverter()
        .insertFirstMiddleHandler(ByteStringConvertHandler.INSTANCE)
        .withSuffixHandler(ProtobufBeanConvertHandler.INSTANCE);
    private static final GekBeanCopier COPIER = ProtobufBeanConvertHandler.INSTANCE.getCopier();

    /**
     * Returns bean resolver supports protobuf based on {@link GekDataResolver#defaultResolver()}.
     *
     * @return bean resolver supports protobuf based on {@link GekDataResolver#defaultResolver()}
     */
    public static GekDataResolver protobufBeanResolver() {
        return RESOLVER;
    }

    /**
     * Returns object converter supports protobuf based on {@link GekConverter#defaultConverter()}.
     *
     * @return object converter supports protobuf based on {@link GekConverter#defaultConverter()}
     */
    public static GekConverter protobufConverter() {
        return CONVERTER;
    }

    /**
     * Returns bean copier supports protobuf based on {@link GekBeanCopier#defaultCopier()}.
     *
     * @return bean copier supports protobuf based on {@link GekBeanCopier#defaultCopier()}
     */
    public static GekBeanCopier protobufBeanCopier() {
        return COPIER;
    }
}
