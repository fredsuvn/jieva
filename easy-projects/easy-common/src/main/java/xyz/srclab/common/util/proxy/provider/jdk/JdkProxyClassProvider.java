package xyz.srclab.common.util.proxy.provider.jdk;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapHelper;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.ConstructorHelper;
import xyz.srclab.common.reflect.ReflectConstants;
import xyz.srclab.common.util.proxy.ProxyClassProvider;
import xyz.srclab.common.util.proxy.ProxyClass;
import xyz.srclab.common.util.proxy.ProxyClassBuilder;
import xyz.srclab.common.util.proxy.ProxyMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class JdkProxyClassProvider implements ProxyClassProvider {

    public static final JdkProxyClassProvider INSTANCE = new JdkProxyClassProvider();

    @Override
    public <T> ProxyClassBuilder<T> newBuilder(Class<T> type) {
        return new JdkProxyClassBuilder<>(type);
    }

    private static final class JdkProxyClassBuilder<T>
            extends CachedBuilder<ProxyClass<T>> implements ProxyClassBuilder<T> {

        private final Class<T> type;
        private final Map<Predicate<Method>, ProxyMethod> proxyMethodMap = new LinkedHashMap<>();

        public JdkProxyClassBuilder(Class<T> type) {
            this.type = type;
        }

        @Override
        public ProxyClassBuilder<T> proxyMethod(Predicate<Method> methodPredicate, ProxyMethod proxyMethod) {
            proxyMethodMap.put(methodPredicate, proxyMethod);
            this.updateState();
            return this;
        }

        @Override
        protected ProxyClass<T> buildNew() {
            return new JdkProxyClass<>(type, proxyMethodMap);
        }
    }

    private static final class JdkProxyClass<T> implements ProxyClass<T> {

        private final Class<?> type;
        private final Map<Method, ProxyMethod> methodMap;

        private JdkProxyClass(Class<?> type, Map<Predicate<Method>, ProxyMethod> proxyMethodMap) {
            this.type = type;
            if (proxyMethodMap.isEmpty()) {
                this.methodMap = Collections.emptyMap();
                return;
            }
            Map<Method, ProxyMethod> methodMap = new LinkedHashMap<>();
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                proxyMethodMap.forEach((predicate, proxyMethod) -> {
                    if (predicate.test(method)) {
                        methodMap.put(method, proxyMethod);
                    }
                });
            }
            this.methodMap = MapHelper.immutable(methodMap);
        }

        @Override
        public T newInstance() {
            if (methodMap.isEmpty()) {
                return ConstructorHelper.newInstance(type);
            }
            Object instance = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                    (object, method, args) -> {
                        Object[] realArgs = ArrayUtils.isEmpty(args) ? ReflectConstants.EMPTY_ARGUMENTS : args;
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
            if (methodMap.isEmpty()) {
                return ConstructorHelper.newInstance(type, parameterTypes, arguments);
            }
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
