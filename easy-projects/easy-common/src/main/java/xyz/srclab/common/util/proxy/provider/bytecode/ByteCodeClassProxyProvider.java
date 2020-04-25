package xyz.srclab.common.util.proxy.provider.bytecode;

import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.bytecode.EnhancedClass;
import xyz.srclab.common.bytecode.provider.ByteCodeProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManager;
import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.util.proxy.ClassProxy;
import xyz.srclab.common.util.proxy.provider.ClassProxyProvider;
import xyz.srclab.common.reflect.method.ProxyMethod;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ByteCodeClassProxyProvider implements ClassProxyProvider {

    public static final ByteCodeClassProxyProvider INSTANCE = new ByteCodeClassProxyProvider();

    @Override
    public <T> ClassProxy.Builder<T> newBuilder(Class<T> type) {
        return new ByteCodeClassProxyBuilder<>(type);
    }

    private static final class ByteCodeClassProxyBuilder<T>
            extends CachedBuilder<ClassProxy<T>> implements ClassProxy.Builder<T> {

        private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManager.INSTANCE.getProvider();

        private final Class<T> type;
        private final List<Pair<Predicate<Method>, ProxyMethod>> predicatePairs = new LinkedList<>();

        public ByteCodeClassProxyBuilder(Class<T> type) {
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
            EnhancedClass.Builder<T> proxyClassBuilder = byteCodeProvider.newEnhancedClassBuilder(type);
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                for (Pair<Predicate<Method>, ProxyMethod> predicatePair : predicatePairs) {
                    if (predicatePair.get0().test(method)) {
                        proxyClassBuilder.overrideMethod(
                                method.getName(), method.getParameterTypes(), predicatePair.get1());
                    }
                }
            }
            EnhancedClass<T> enhancedClass = proxyClassBuilder.build();
            return new ClassProxy<T>() {
                @Override
                public T newInstance() {
                    return enhancedClass.newInstance();
                }

                @Override
                public T newInstance(Class<?>[] parameterTypes, Object[] arguments) {
                    return enhancedClass.newInstance(parameterTypes, arguments);
                }
            };
        }
    }
}
