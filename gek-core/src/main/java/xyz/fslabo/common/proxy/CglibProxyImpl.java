package xyz.fslabo.common.proxy;

import net.sf.cglib.proxy.*;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.collect.JieColl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

final class CglibProxyImpl<T> implements GekProxy<T> {

    private final Enhancer enhancer;

    CglibProxyImpl(
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> superInterfaces,
        Map<Predicate<Method>, GekProxyMethod> proxyMap
    ) {
        Enhancer enhancer = new Enhancer();
        boolean inherited = false;
        if (superClass != null) {
            enhancer.setSuperclass(superClass);
            inherited = true;
        }
        if (JieColl.isNotEmpty(superInterfaces)) {
            enhancer.setInterfaces(JieColl.toArray(superInterfaces, Class.class));
            inherited = true;
        }
        if (!inherited) {
            throw new GekProxyException("No super class or interface to be proxied.");
        }
        List<MethodInterceptor> interceptorList = new ArrayList<>();
        interceptorList.add((obj, method, args, proxy) -> proxy.invokeSuper(obj, args));
        Predicate<Method>[] predicates = proxyMap.keySet().toArray(new Predicate[0]);
        for (Predicate<Method> predicate : predicates) {
            GekProxyMethod proxy = proxyMap.get(predicate);
            MethodInterceptor interceptor = new MethodInterceptorImpl(proxy);
            interceptorList.add(interceptor);
        }
        CallbackFilter callbackFilter = method -> {
            for (int i = 0; i < predicates.length; i++) {
                if (predicates[i].test(method)) {
                    return i + 1;
                }
            }
            return 0;
        };
        enhancer.setCallbackFilter(callbackFilter);
        enhancer.setCallbacks(interceptorList.toArray(new Callback[0]));
        this.enhancer = enhancer;
    }

    @Override
    public T newInstance() {
        return (T) enhancer.create();
    }

    private static final class MethodInterceptorImpl implements MethodInterceptor {

        private final GekProxyMethod gekProxyMethod;

        private MethodInterceptorImpl(GekProxyMethod gekProxyMethod) {
            this.gekProxyMethod = gekProxyMethod;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
            return gekProxyMethod.invokeProxy(args, method, args1 -> proxy.invokeSuper(obj, args1));
        }
    }
}
