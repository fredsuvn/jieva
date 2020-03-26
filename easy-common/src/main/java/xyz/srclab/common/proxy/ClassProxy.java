package xyz.srclab.common.proxy;

import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.bytecode.proxy.ProxyClass;
import xyz.srclab.common.reflect.MethodBody;
import xyz.srclab.common.reflect.ReflectHelper;
import xyz.srclab.common.tuple.Pair;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public interface ClassProxy<T> {

    static <T> Builder<T> newBuilder(Class<T> type) {
        return Builder.newBuilder(type);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] arguments);

    class Builder<T> extends CacheStateBuilder<ClassProxy<T>> {
        public static <T> Builder<T> newBuilder(Class<T> type) {
            return new Builder<>(type);
        }

        private final Class<T> type;
        private final List<Pair<Predicate<Method>, MethodBody<?>>> predicatePairs = new LinkedList<>();

        public Builder(Class<T> type) {
            this.type = type;
        }

        public Builder<T> proxyMethod(
                String methodName, Class<?>[] parameterTypes, MethodBody<?> methodBody) {
            return proxyMethod(
                    method -> method.getName().equals(methodName)
                            && Arrays.equals(method.getParameterTypes(), parameterTypes),
                    methodBody
            );
        }

        public Builder<T> proxyMethod(
                Predicate<Method> methodPredicate, MethodBody<?> methodBody) {
            predicatePairs.add(Pair.of(methodPredicate, methodBody));
            return this;
        }

        @Override
        protected ClassProxy<T> buildNew() {
            ProxyClass<T> proxyClass = buildProxyClass();
            return new ClassProxy<T>() {
                @Override
                public T newInstance() {
                    return proxyClass.newInstance();
                }

                @Override
                public T newInstance(Class<?>[] parameterTypes, Object[] arguments) {
                    return proxyClass.newInstance(parameterTypes, arguments);
                }
            };
        }

        private ProxyClass<T> buildProxyClass() {
            ProxyClass.Builder<T> builder = ProxyClass.newBuilder(type);
            List<Method> methods = ReflectHelper.getOverrideableMethods(type);
            for (Method method : methods) {
                for (Pair<Predicate<Method>, MethodBody<?>> predicatePair : predicatePairs) {
                    if (predicatePair.get0().test(method)) {
                        builder.overrideMethod(method.getName(), method.getParameterTypes(), predicatePair.get1());
                    }
                }
            }
            return builder.build();
        }
    }
}
