package xyz.srclab.common.protobuf;

import xyz.srclab.common.bean.FsBeanCopier;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.convert.handlers.BeanConvertHandler;

/**
 * Utilities for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * This class depends on protobuf libs in the runtime.
 */
public class FsProtobuf {

    private static FsBeanResolver RESOLVER = FsBeanResolver.defaultResolver().withHandler(ProtobufResolveHandler.INSTANCE);
    private static FsBeanCopier COPIER = FsBeanCopier.defaultCopier().toBuilder().beanResolver(RESOLVER).build();
    private static FsConverter CONVERTER = FsConverter.defaultConverter().withSuffixHandler(new BeanConvertHandler(COPIER));

    /**
     * Returns bean resolver supports protobuf based on {@link FsBeanResolver#defaultResolver()}.
     */
    public static FsBeanResolver protobufBeanResolver() {
        return RESOLVER;
    }

    /**
     * Returns bean copier supports protobuf based on {@link FsBeanCopier#defaultCopier()}.
     */
    public static FsBeanCopier protobufBeanCopier() {
        return COPIER;
    }

    /**
     * Returns object converter supports protobuf based on {@link FsConverter#defaultConverter()}.
     */
    public static FsConverter protobufConverter() {
        return CONVERTER;
    }
}
