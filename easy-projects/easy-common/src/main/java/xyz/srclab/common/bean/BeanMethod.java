package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Immutable
public interface BeanMethod {

    String getName();

    Class<?>[] getParameterTypes();

    Type[] getGenericParameterTypes();

    int getParameterCount();

    Class<?> getReturnType();

    Type getGenericReturnType();

    Method getMethod();

    @Nullable
    Object invoke(Object bean, Object... args);
}
