package xyz.srclab.common.convert;

/**
 * @author sunqian
 */
final class ConvertSupport {

    static Converter defaultConverter() {
        return ConverterHolder.INSTANCE;
    }

    private static final class ConverterHolder {

        public static final Converter INSTANCE = Converter.newBuilder()
                .handler(ConvertHandler.defaultHandler())
                .build();
    }
}
