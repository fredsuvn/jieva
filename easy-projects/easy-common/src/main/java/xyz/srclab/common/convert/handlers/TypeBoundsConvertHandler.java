package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * @author sunqian
 */
public class TypeBoundsConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        return null;
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        if (to instanceof TypeVariable) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) to;
            return converter.convert(from, typeVariable.getBounds()[0]);
        }
        if (to instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) to;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length > 0) {
                return converter.convert(from, upperBounds[0]);
            }
            return converter.convert(from, Object.class);
        }
        return null;
    }
}
