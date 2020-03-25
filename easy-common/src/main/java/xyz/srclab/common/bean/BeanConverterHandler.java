package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.lang.TypeRef;

import java.lang.reflect.Type;

public interface BeanConverterHandler {

    boolean supportConvert(@Nullable Object from, Type to, BeanOperator beanOperator);

    default boolean supportConvert(@Nullable Object from, Class<?> to, BeanOperator beanOperator) {
        return supportConvert(from, (Type) to, beanOperator);
    }

    default boolean supportConvert(@Nullable Object from, TypeRef<?> to, BeanOperator beanOperator) {
        return supportConvert(from, to.getType(), beanOperator);
    }

    @Nullable
    <T> T convert(@Nullable Object from, Type to, BeanOperator beanOperator);

    @Nullable
    default <T> T convert(@Nullable Object from, Class<T> to, BeanOperator beanOperator) {
        return convert(from, (Type) to, beanOperator);
    }

    @Nullable
    default <T> T convert(@Nullable Object from, TypeRef<T> to, BeanOperator beanOperator) {
        return convert(from, to.getType(), beanOperator);
    }
}
