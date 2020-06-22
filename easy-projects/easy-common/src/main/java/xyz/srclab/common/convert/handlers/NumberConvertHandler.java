package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public class NumberConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {

        return null;
    }

    private byte toByte(Object from) {
        if (from instanceof Number) {
            return from.by
        }
    }

    private byte toShort(Object from) {

    }

    private byte toChar(Object from) {

    }

    private byte toInt(Object from) {

    }

    private byte toLong(Object from) {

    }

    private byte toFloat(Object from) {

    }

    private byte toDouble(Object from) {

    }

    private byte toBigInteger(Object from) {

    }

    private byte toBigDecimal(Object from) {

    }
}
