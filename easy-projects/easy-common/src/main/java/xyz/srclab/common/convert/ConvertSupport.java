package xyz.srclab.common.convert;

import xyz.srclab.common.convert.handlers.NumberConvertHandler;

/**
 * @author sunqian
 */
final class ConvertSupport {

    static Converter defaultConverter() {
        return ConverterHolder.INSTANCE;
    }

    private static final class ConverterHolder {

        public static final Converter INSTANCE = Converter.newBuilder()
                .handlers(
                        new NumberConvertHandler()
                )
                .build();
    }
}
