package xyz.srclab.common.proxy.provider.jdk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.proxy.ClassProxy;
import xyz.srclab.common.proxy.provider.ClassProxyProvider;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.method.MethodHelper;
import xyz.srclab.common.reflect.method.ProxyMethod;

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
    public <T> ClassProxy.Builder<T> newBuilder(Class<T> type) {
        return new JdkClassProxyBuilder<>(type);
    }

    private static final class JdkClassProxyBuilder<T>
            extends CachedBuilder<ClassProxy<T>> implements ClassProxy.Builder<T> {

        private final Class<T> type;
        private final List<Pair<Predicate<Method>, ProxyMethod>> predicatePairs = new LinkedList<>();

        public JdkClassProxyBuilder(Class<T> type) {
            this.type = type;
        }

        @Override
        public ClassProxy.Builder<T> proxyMethod(Predicate<Method> methodPredicate, ProxyMethod proxyMethod) {
            this.updateState();
            predicatePairs.add(Pair.of(methodPredicate, proxyMethod));
            return this;
        }

        @Override
        protected ClassProxy<T> buildNew() {
            return new JdkClassProxy<>(type, predicatePairs);
        }
    }

    private static final class JdkClassProxy<T> implements ClassProxy<T> {

        private final Class<?> type;
        private final Map<Method, ProxyMethod> methodMap;

        private JdkClassProxy(Class<?> type, List<Pair<Predicate<Method>, ProxyMethod>> predicatePairs) {
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
                        return proxyMethod.invoke(object, realArgs, method, new MethodInvoker() {
                            @Override
                            public @Nullable Object invoke(Object object, Object... args) {
                                try {
                                    return method.invoke(object, args);
                                } catch (Exception e) {
                                    throw new ExceptionWrapper(e);
                                }
                            }
                        });
                    });
            return (T) instance;
        }

        @Override
        public T newInstance(Class<?>[] parameterTypes, Object[] arguments) {
            throw new UnsupportedOperationException("JDK proxy doesn't support this");
        }
    }
}
