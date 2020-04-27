package xyz.srclab.common.util.proxy;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

@Immutable
public interface ProxyClass<T> {

    static <T> Builder<T> newBuilder(Class<T> type) {
        return ProxyClassSupport.newBuilder(type);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    Class<T> getProxyClass();

    interface Builder<T> {

        default Builder<T> proxyMethod(String methodName, Class<?>[] parameterTypes, ProxyMethod proxyMethod) {
            return proxyMethod(
                    method -> method.getName().equals(methodName)
                            && Arrays.equals(method.getParameterTypes(), parameterTypes),
                    proxyMethod
            );
        }

        Builder<T> proxyMethod(Predicate<Method> methodPredicate, ProxyMethod proxyMethod);

        ProxyClass<T> build();
    }
}
