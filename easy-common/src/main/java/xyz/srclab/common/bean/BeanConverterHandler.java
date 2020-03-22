package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;

public interface BeanConverterHandler {

    boolean supportConvert(@Nullable Object from, Class<?> to);

    @Nullable
    <T> T convert(@Nullable Object from, Class<T> to);
}
