package xyz.srclab.common.util.proxy.provider.jdk;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.ClassKit;
import xyz.srclab.common.util.proxy.*;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.Predicate;

public class JdkProxyClassProvider implements ProxyClassProvider {

    public static final JdkProxyClassProvider INSTANCE = new JdkProxyClassProvider();

    @Override
    public <T> ProxyClassBuilder<T> newBuilder(Class<T> type) {
        return new JdkProxyClassBuilder<>(type);
    }

    private static final class JdkProxyClassBuilder<T> extends AbstractProxyClassBuilder<T> {

        public JdkProxyClassBuilder(Class<T> type) {
            super(type);
        }

        @Override
        protected ProxyClass<T> buildNew() {
            return new JdkProxyClass<>(type, proxyMethodMap);
        }
    }

    private static final class JdkProxyClass<T> extends AbstractProxyClass<T> {

        private JdkProxyClass(Class<?> type, Map<Predicate<Method>, ProxyMethod> proxyMethodMap) {
            super(type, proxyMethodMap);
        }

        @Override
        public T newInstance() {
            if (methodMap.isEmpty()) {
                return ClassKit.newInstance(type);
            }
            Object instance = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                    (object, method, args) -> {
                        Object[] realArgs = ArrayUtils.isEmpty(args) ? ArrayUtils.EMPTY_OBJECT_ARRAY : args;
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
                return ClassKit.newInstance(type, parameterTypes, arguments);
            }
            throw new UnsupportedOperationException("JDK proxy doesn't support this");
        }

        @Override
        public Class<T> getProxyClass() {
            throw new UnsupportedOperationException("JDK proxy doesn't support this");
        }
    }

    private static final class UnsupportedMethodInvoker implements SuperInvoker {

        private static final String UNSUPPORTED_MESSAGE = "JDK proxy only supports proxying for interface, " +
                "so the interface method is abstract and cannot be invoked.";

        private static final UnsupportedMethodInvoker INSTANCE = new UnsupportedMethodInvoker();

        @Override
        public @Nullable Object invoke(@Nullable Object object, Object... args) {
            throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
        }
    }
}
