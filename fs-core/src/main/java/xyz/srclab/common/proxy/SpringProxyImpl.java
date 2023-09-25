package xyz.srclab.common.proxy;

import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.*;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.collect.FsCollect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

final class SpringProxyImpl<T> implements FsProxy<T> {

    private final Enhancer enhancer;

    SpringProxyImpl(
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> superInterfaces,
        Map<Predicate<Method>, FsProxyMethod> proxyMap
    ) {
        Enhancer enhancer = new Enhancer();
        boolean inherited = false;
        if (superClass != null) {
            enhancer.setSuperclass(superClass);
            inherited = true;
        }
        if (FsCollect.isNotEmpty(superInterfaces)) {
            enhancer.setInterfaces(FsCollect.toArray(superInterfaces, Class.class));
            inherited = true;
        }
        if (!inherited) {
            throw new FsProxyException("No super class or interface to be proxied.");
        }
        List<MethodInterceptor> interceptorList = new ArrayList<>();
        interceptorList.add((obj, method, args, proxy) -> proxy.invokeSuper(obj, args));
        Predicate<Method>[] predicates = proxyMap.keySet().toArray(new Predicate[0]);
        for (Predicate<Method> predicate : predicates) {
            FsProxyMethod proxy = proxyMap.get(predicate);
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
        enhancer.setNamingPolicy(new SpringNamingPolicy());
        this.enhancer = enhancer;
    }

    private static final class MethodInterceptorImpl implements MethodInterceptor {

        private final FsProxyMethod fsProxyMethod;

        private MethodInterceptorImpl(FsProxyMethod fsProxyMethod) {
            this.fsProxyMethod = fsProxyMethod;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
            return fsProxyMethod.invokeProxy(args, method, args1 -> proxy.invokeSuper(obj, args1));
        }
    }

    @Override
    public T newInstance() {
        return (T) enhancer.create();
    }
}
