package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Immutable
public interface BeanMethod extends BeanMember {

    Class<?>[] getParameterTypes();

    Type[] getGenericParameterTypes();

    int getParameterCount();

    Class<?> getReturnType();

    Type getGenericReturnType();

    Method getMethod();

    MethodInvoker getMethodInvoker();

    @Nullable
    default Object invoke(Object bean, Object... args) {
        return getMethodInvoker().invoke(bean, args);
    }
}
