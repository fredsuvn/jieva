package xyz.srclab.common.proxy.provider.bytecode;

import xyz.srclab.bytecode.provider.ByteCodeProvider;
import xyz.srclab.bytecode.provider.ByteCodeProviderManager;
import xyz.srclab.bytecode.proxy.ProxyClass;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.proxy.ClassProxy;
import xyz.srclab.common.proxy.provider.ClassProxyProvider;
import xyz.srclab.common.reflect.method.ProxyMethod;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ByteCodeClassProxyProvider implements ClassProxyProvider {

    public static final ByteCodeClassProxyProvider INSTANCE = new ByteCodeClassProxyProvider();

    @Override
    public <T> ClassProxy.Builder<T> newBuilder(Class<T> type) {
        return new BytecodeClassProxyBuilder<>(type);
    }

    private static final class BytecodeClassProxyBuilder<T>
            extends CacheStateBuilder<ClassProxy<T>> implements ClassProxy.Builder<T> {

        private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManager.INSTANCE.getProvider();

        private final Class<T> type;
        private final List<Pair<Predicate<Method>, ProxyMethod>> predicatePairs = new LinkedList<>();

        public BytecodeClassProxyBuilder(Class<T> type) {
            this.type = type;
        }

        @Override
        public ClassProxy.Builder<T> proxyMethod(Predicate<Method> methodPredicate, ProxyMethod proxyMethod) {
            this.changeState();
            predicatePairs.add(Pair.of(methodPredicate, proxyMethod));
            return this;
        }

        @Override
        protected ClassProxy<T> buildNew() {
            ProxyClass.Builder<T> proxyClassBuilder = byteCodeProvider.newProxyClassBuilder(type);
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                for (Pair<Predicate<Method>, ProxyMethod> predicatePair : predicatePairs) {
                    if (predicatePair.get0().test(method)) {
                        proxyClassBuilder.overrideMethod(
                                method.getName(), method.getParameterTypes(), predicatePair.get1());
                    }
                }
            }
            ProxyClass<T> proxyClass = proxyClassBuilder.build();
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
    }
}
