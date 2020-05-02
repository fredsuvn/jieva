package xyz.srclab.common.util.proxy.provider.jdk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.method.MethodHelper;
import xyz.srclab.common.util.proxy.ClassProxyProvider;
import xyz.srclab.common.util.proxy.ProxyClass;
import xyz.srclab.common.util.proxy.ProxyMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class JdkClassProxyProvider implements ClassProxyProvider {

    public static final JdkClassProxyProvider INSTANCE = new JdkClassProxyProvider();

    @Override
    public <T> ProxyClass.Builder<T> newBuilder(Class<T> type) {
        return new JdkClassProxyBuilder<>(type);
    }

    private static final class JdkClassProxyBuilder<T>
            extends CachedBuilder<ProxyClass<T>> implements ProxyClass.Builder<T> {

        private final Class<T> type;
        private final List<Pair<Predicate<Method>, ProxyMethod>> predicatePairs = new LinkedList<>();

        public JdkClassProxyBuilder(Class<T> type) {
            this.type = type;
        }

        @Override
        public ProxyClass.Builder<T> proxyMethod(Predicate<Method> methodPredicate, ProxyMethod proxyMethod) {
            predicatePairs.add(Pair.of(methodPredicate, proxyMethod));
            this.updateState();
            return this;
        }

        @Override
        protected ProxyClass<T> buildNew() {
            return new JdkProxyClass<>(type, predicatePairs);
        }
    }

    private static final class JdkProxyClass<T> implements ProxyClass<T> {

        private final Class<?> type;
        private final Map<Method, ProxyMethod> methodMap;

        private JdkProxyClass(Class<?> type, List<Pair<Predicate<Method>, ProxyMethod>> predicatePairs) {
            this.type = type;
            Map<Method, ProxyMethod> map = new LinkedHashMap<>();
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                for (Pair<Predicate<Method>, ProxyMethod> predicatePair : predicatePairs) {
                    if (predicatePair.get0().test(method)) {
                        map.put(method, predicatePair.get1());
                    }
                }
            }
            this.methodMap = MapHelper.immutable(map);
        }

        @Override
        public T newInstance() {
            Object instance = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                    (object, method, args) -> {
                        Object[] realArgs = args == null ? MethodHelper.EMPTY_ARGUMENTS : args;
                        ProxyMethod proxyMethod = methodMap.get(method);
                        if (proxyMethod == null) {
                            return method.invoke(object, realArgs);
                        }
                        return proxyMethod.invoke(object, realArgs, method, UnsupportedMethodInvoker.INSTANCE);
                    });
            return (T) instance;
        }

        @Override
        public T newInstance(Class<?>[] parameterTypes, Object[] arguments) {
            throw new UnsupportedOperationException("JDK proxy doesn't support this");
        }

        @Override
        public Class<T> getProxyClass() {
            throw new UnsupportedOperationException("JDK proxy doesn't support this");
        }

        private static final class UnsupportedMethodInvoker implements MethodInvoker {

            private static final String UNSUPPORTED_MESSAGE = "JDK proxy only supports proxying for interface, " +
                    "so the interface method is abstract and cannot be invoked.";

            private static final UnsupportedMethodInvoker INSTANCE = new UnsupportedMethodInvoker();

            @Override
            public Method getMethod() {
                throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
            }

            @Override
            public @Nullable Object invoke(@Nullable Object object, Object... args) {
                throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
            }
        }
    }
}
