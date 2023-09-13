package xyz.srclab.common.protobuf;

import xyz.srclab.common.bean.FsBeanCopier;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.convert.FsConverter;

/**
 * Utilities for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * This class depends on protobuf libs in the runtime.
 */
public class FsProtobuf {

    private static final FsBeanResolver RESOLVER = FsBeanResolver.defaultResolver()
        .insertFirstHandler(ProtobufResolveHandler.INSTANCE);
    private static final FsConverter CONVERTER = FsConverter.defaultConverter()
        .insertFirstMiddleHandler(ByteStringConvertHandler.INSTANCE)
        .withSuffixHandler(ProtobufBeanConvertHandler.INSTANCE);
    private static final FsBeanCopier COPIER = FsBeanCopier.defaultCopier()
        .toBuilder().beanResolver(RESOLVER).converter(CONVERTER).build();

    /**
     * Returns bean resolver supports protobuf based on {@link FsBeanResolver#defaultResolver()}.
     */
    public static FsBeanResolver protobufBeanResolver() {
        return RESOLVER;
    }

    /**
     * Returns object converter supports protobuf based on {@link FsConverter#defaultConverter()}.
     */
    public static FsConverter protobufConverter() {
        return CONVERTER;
    }

    /**
     * Returns bean copier supports protobuf based on {@link FsBeanCopier#defaultCopier()}.
     */
    public static FsBeanCopier protobufBeanCopier() {
        return COPIER;
    }
}
