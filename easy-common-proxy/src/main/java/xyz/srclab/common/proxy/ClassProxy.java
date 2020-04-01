package xyz.srclab.common.proxy;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.reflect.method.MethodBody;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

@ThreadSafe
public interface ClassProxy<T> {

    static <T> Builder<T> newBuilder(Class<T> type) {
        return ClassProxyBuilderHelper.newBuilder(type);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] arguments);

    interface Builder<T> {

        default Builder<T> proxyMethod(String methodName, Class<?>[] parameterTypes, MethodBody<?> methodBody) {
            return proxyMethod(
                    method -> method.getName().equals(methodName)
                            && Arrays.equals(method.getParameterTypes(), parameterTypes),
                    methodBody
            );
        }

        Builder<T> proxyMethod(Predicate<Method> methodPredicate, MethodBody<?> methodBody);

        ClassProxy<T> build();
    }
}
