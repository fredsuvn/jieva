package xyz.fslabo.common.data.protobuf;

import xyz.fslabo.common.bean.BeanResolver;
import xyz.fslabo.common.mapper.JieMapper;

/**
 * Utilities for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * This class depends on protobuf libs in the runtime.
 */
public class GekProtobuf {

    private static final BeanResolver RESOLVER = BeanResolver.defaultResolver()
        .withFirstHandler(ProtobufResolveHandler.INSTANCE);
    private static final JieMapper CONVERTER = JieMapper.defaultMapper()
        .insertFirstMiddleHandler(ByteStringConvertHandler.INSTANCE)
        .withSuffixHandler(ProtobufBeanConvertHandler.INSTANCE);
    private static final GekBeanCopier COPIER = ProtobufBeanConvertHandler.INSTANCE.getCopier();

    /**
     * Returns bean resolver supports protobuf based on {@link BeanResolver#defaultResolver()}.
     *
     * @return bean resolver supports protobuf based on {@link BeanResolver#defaultResolver()}
     */
    public static BeanResolver protobufBeanResolver() {
        return RESOLVER;
    }

    /**
     * Returns object converter supports protobuf based on {@link JieMapper#defaultMapper()}.
     *
     * @return object converter supports protobuf based on {@link JieMapper#defaultMapper()}
     */
    public static JieMapper protobufConverter() {
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
