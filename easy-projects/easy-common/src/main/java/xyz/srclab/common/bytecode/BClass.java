package xyz.srclab.common.bytecode;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author sunqian
 */
public interface BClass<T> {

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    interface Builder<T> {

        Builder<T> addInterface(Class<?> interfaceClass);

        default Builder<T> addInterfaces(Class<?>... interfaces) {
            return addInterfaces(Arrays.asList(interfaces));
        }

        Builder<T> addInterfaces(Iterable<Class<?>> interfaces);

        Builder<T> addConstructor(Class<?>[] parameterTypes, ConstructorBody<T> constructorBody);

        default Builder<T> addProperty(String propertyName, Class<?> type) {
            return addProperty(propertyName, type, true, true);
        }

        Builder<T> addProperty(String propertyName, Class<?> type, boolean readable, boolean writeable);

        Builder<T> addMethod(String methodName, Class<?>[] parameterTypes, MethodBody methodBody);
    }

    interface ConstructorBody<T> {

        @Nullable
        Object invoke(@Nullable Constructor<T> constructor, Object... args);
    }

    interface MethodBody {

        @Nullable
        Object invoke(Object instance, @Nullable Method method, Object... args);
    }
}
