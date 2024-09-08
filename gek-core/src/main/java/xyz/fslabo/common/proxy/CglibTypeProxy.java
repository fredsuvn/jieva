package xyz.fslabo.common.proxy;

import net.sf.cglib.proxy.*;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.coll.JieColl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

final class CglibTypeProxy<T> implements TypeProxy<T> {

    private final Enhancer enhancer;

    CglibTypeProxy(
        ClassLoader loader,
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> superInterfaces,
        Map<Predicate<Method>, ProxyInvoker> proxyMap
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
            throw new ProxyException("No super class or interface.");
        }
        // if (superClass == null) {
        //     Object interfaceProxy = JdkTypeProxy.createInterfaceProxy(
        //         Jie.orDefault(loader, getClass().getClassLoader()),
        //         superInterfaces,
        //         proxyMap
        //     );
        //     System.out.println(">>>>>" + interfaceProxy.getClass());
        //     System.out.println(">>>>>" + Modifier.isFinal(interfaceProxy.getClass().getModifiers()));
        //     enhancer.setSuperclass(interfaceProxy.getClass());
        // }
        List<MethodInterceptor> interceptorList = new ArrayList<>();
        interceptorList.add((obj, method, args, proxy) -> {
            return  proxy.invokeSuper(obj, args);
        });
        Predicate<Method>[] predicates = proxyMap.keySet().toArray(new Predicate[0]);
        for (Predicate<Method> predicate : predicates) {
            ProxyInvoker proxy = proxyMap.get(predicate);
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
        enhancer.setClassLoader(loader);
        this.enhancer = enhancer;
    }

    @Override
    public T newInstance() {
        return Jie.as(enhancer.create());
    }

    private static final class MethodInterceptorImpl<T> implements MethodInterceptor {

        private final ProxyInvoker typeProxyMethod;

        private MethodInterceptorImpl(ProxyInvoker typeProxyMethod) {
            this.typeProxyMethod = typeProxyMethod;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return typeProxyMethod.invoke(obj, method, proxy::invokeSuper, args);
        }
    }
}
