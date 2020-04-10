package xyz.srclab.common.proxy;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.reflect.method.ProxyMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

@Immutable
public interface ClassProxy<T> {

    static <T> Builder<T> newBuilder(Class<T> type) {
        return ClassProxyBuilderHelper.newBuilder(type);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] arguments);

    interface Builder<T> {

        default Builder<T> proxyMethod(String methodName, Class<?>[] parameterTypes, ProxyMethod proxyMethod) {
            return proxyMethod(
                    method -> method.getName().equals(methodName)
                            && Arrays.equals(method.getParameterTypes(), parameterTypes),
                    proxyMethod
            );
        }

        Builder<T> proxyMethod(Predicate<Method> methodPredicate, ProxyMethod proxyMethod);

        ClassProxy<T> build();
    }
}
