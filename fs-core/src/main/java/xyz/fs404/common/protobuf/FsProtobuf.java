package xyz.fs404.common.protobuf;

import xyz.fs404.common.bean.FsBeanCopier;
import xyz.fs404.common.bean.FsBeanResolver;
import xyz.fs404.common.convert.FsConverter;

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
    private static final FsBeanCopier COPIER = ProtobufBeanConvertHandler.INSTANCE.getCopier();

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
