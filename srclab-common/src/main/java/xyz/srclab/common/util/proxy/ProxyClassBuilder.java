package xyz.srclab.common.util.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author sunqian
 */
public interface ProxyClassBuilder<T> {

    default ProxyClassBuilder<T> proxyMethod(
            String methodName, Class<?>[] parameterTypes, ProxyMethod proxyMethod) {
        return proxyMethod(
                method -> method.getName().equals(methodName)
                        && Arrays.equals(method.getParameterTypes(), parameterTypes),
                proxyMethod
        );
    }

    ProxyClassBuilder<T> proxyMethod(Predicate<Method> methodPredicate, ProxyMethod proxyMethod);

    ProxyClass<T> build();
}
