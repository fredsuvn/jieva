package xyz.srclab.common.bytecode.provider.util.proxy.cglib.cglib;

import net.sf.cglib.proxy.*;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.util.count.Counter;
import xyz.srclab.common.util.proxy.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author sunqian
 */
public class CglibProxyClassProvider implements ProxyClassProvider {

    @Override
    public <T> ProxyClassBuilder<T> newBuilder(Class<T> type) {
        return new CglibProxyClassBuilder<>(type);
    }

    private static final class CglibProxyClassBuilder<T> extends AbstractProxyClassBuilder<T> {

        protected CglibProxyClassBuilder(Class<T> type) {
            super(type);
        }

        @Override
        protected ProxyClass<T> buildNew() {
            return new CglibProxyClass<>(type, proxyMethodMap);
        }
    }

    private static final class CglibProxyClass<T> extends AbstractProxyClass<T> {

        private final Enhancer enhancer;

        protected CglibProxyClass(Class<?> type, Map<Predicate<Method>, ProxyMethod> proxyMethodMap) {
            super(type, proxyMethodMap);
            this.enhancer = buildEnhancer();
        }

        private Enhancer buildEnhancer() {
            enhancer.setSuperclass(type);
            Callback[] callbacks = new Callback[methodMap.size() + 1];
            callbacks[0] = NoOp.INSTANCE;
            Counter counter = Counter.from(1);
            CallbackFilter callbackFilter = method -> {
                ProxyMethod proxyMethod = methodMap.get(method);
                if (proxyMethod == null) {
                    return 0;
                }
                int index = counter.getIntAndIncrement();
                callbacks[index] = newMethodInterceptor(proxyMethod);
                return index;
            };
            enhancer.setCallbacks(callbacks);
            enhancer.setCallbackFilter(callbackFilter);
            return enhancer;
        }

        private MethodInterceptor newMethodInterceptor(ProxyMethod proxyMethod) {
            return (obj, method, args, proxy) -> proxyMethod.invoke(obj, args, method,
                    (o, as) -> {
                        try {
                            return proxy.invokeSuper(o, as);
                        } catch (Throwable throwable) {
                            throw new ExceptionWrapper(throwable);
                        }
                    });
        }

        @Override
        public T newInstance() {
            return (T) enhancer.create();
        }

        @Override
        public T newInstance(Class<?>[] parameterTypes, Object[] args) {
            return (T) enhancer.create(parameterTypes, args);
        }

        @Override
        public Class<T> getProxyClass() {
            return enhancer.createClass();
        }
    }
}
