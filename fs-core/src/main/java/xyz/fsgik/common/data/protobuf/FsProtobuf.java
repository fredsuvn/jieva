package xyz.fsgik.common.data.protobuf;

import xyz.fsgik.common.bean.FsBeanCopier;
import xyz.fsgik.common.bean.FsBeanResolver;
import xyz.fsgik.common.convert.FsConverter;

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
     *
     * @return bean resolver supports protobuf based on {@link FsBeanResolver#defaultResolver()}
     */
    public static FsBeanResolver protobufBeanResolver() {
        return RESOLVER;
    }

    /**
     * Returns object converter supports protobuf based on {@link FsConverter#defaultConverter()}.
     *
     * @return object converter supports protobuf based on {@link FsConverter#defaultConverter()}
     */
    public static FsConverter protobufConverter() {
        return CONVERTER;
    }

    /**
     * Returns bean copier supports protobuf based on {@link FsBeanCopier#defaultCopier()}.
     *
     * @return bean copier supports protobuf based on {@link FsBeanCopier#defaultCopier()}
     */
    public static FsBeanCopier protobufBeanCopier() {
        return COPIER;
    }
}
