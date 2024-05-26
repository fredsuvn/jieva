package xyz.fsgek.common.data.protobuf;

import xyz.fsgek.common.bean.GekBeanResolver;
import xyz.fsgek.common.convert.GekConverter;

/**
 * Utilities for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * This class depends on protobuf libs in the runtime.
 */
public class GekProtobuf {

    private static final GekBeanResolver RESOLVER = GekBeanResolver.defaultResolver()
        .withFirstHandler(ProtobufResolveHandler.INSTANCE);
    private static final GekConverter CONVERTER = GekConverter.defaultConverter()
        .insertFirstMiddleHandler(ByteStringConvertHandler.INSTANCE)
        .withSuffixHandler(ProtobufBeanConvertHandler.INSTANCE);
    private static final GekBeanCopier COPIER = ProtobufBeanConvertHandler.INSTANCE.getCopier();

    /**
     * Returns bean resolver supports protobuf based on {@link GekBeanResolver#defaultResolver()}.
     *
     * @return bean resolver supports protobuf based on {@link GekBeanResolver#defaultResolver()}
     */
    public static GekBeanResolver protobufBeanResolver() {
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
