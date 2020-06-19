package xyz.srclab.common.convert;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;

@Immutable
public interface Converter {

    static Converter defaultConverter() {
        return ConvertSupport.defaultConverter();
    }

    static ConverterBuilder newBuilder() {
        return ConverterBuilder.newBuilder();
    }

    <T> T convert(Object from, Class<T> to) throws UnsupportedOperationException;

    <T> T convert(Object from, Type to) throws UnsupportedOperationException;

    <T> T convert(Object from, TypeRef<T> to) throws UnsupportedOperationException;
}
