package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieSystem;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.invoke.Invoker;
import xyz.fslabo.common.invoke.InvokingException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

final class JdkTypeProxy<T> implements TypeProxy<T> {

    private final ClassLoader loader;
    private final Class<?>[] superInterfaces;
    private final Map<Method, TypeProxyMethod> methodMap;

    JdkTypeProxy(
        ClassLoader loader,
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> superInterfaces,
        Map<Predicate<Method>, TypeProxyMethod> proxyMap
    ) {
        if (JieColl.isEmpty(superInterfaces)) {
            throw new TypeProxyException("No super interface.");
        }
        this.loader = loader;
        this.superInterfaces = JieColl.toArray(superInterfaces, Class.class);
        if (JieColl.isEmpty(proxyMap)) {
            this.methodMap = Collections.emptyMap();
        } else {
            this.methodMap = new HashMap<>();
            for (Class<?> superInterface : superInterfaces) {
                Method[] methods = superInterface.getMethods();
                proxyMap.forEach((predicate, proxy) -> {
                    for (Method method : methods) {
                        if (predicate.test(method)) {
                            methodMap.put(method, proxy);
                        }
                    }
                });
            }
        }
    }

    @Override
    public T newInstance() {
        Object inst = Proxy.newProxyInstance(
            loader == null ? this.getClass().getClassLoader() : loader,
            superInterfaces,
            new InvocationHandlerImpl());
        return Jie.as(inst);
    }

    private final class InvocationHandlerImpl implements InvocationHandler {

        private final Map<Method, MethodHandle> defaultHandles = new HashMap<>();

        {
            for (Class<?> superInterface : superInterfaces) {
                Method[] methods = superInterface.getMethods();
                for (Method method : methods) {
                    if (method.isDefault()) {
                        defaultHandles.put(
                            method,
                            lookup(method).unreflectSpecial(method, method.getDeclaringClass())
                        );
                    }
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (methodMap.isEmpty()) {
                return invokeSuper(proxy, method, args);
            }
            TypeProxyMethod proxyMethod = methodMap.get(method);
            if (proxyMethod == null) {
                return invokeSuper(proxy, method, args);
            }
            return proxyMethod.invokeProxy(proxy, method, (p, as) -> invokeSuper(p, method, as), args);
        }

        private Object invokeSuper(Object proxy, Method method, Object[] args) {
            if (method.isDefault()) {
                try {
                    lookup(method)
                        .unreflectSpecial(method, method.getDeclaringClass())
                        .bindTo(proxy)
                        .invokeWithArguments(args);
                } catch (Throwable e) {
                    throw new InvokingException(e);
                }
            }
            throw new InvokingException("Method is abstract: " + method);
        }

        private MethodHandle getDefaultHandle(Method method) throws Throwable{
            if (defaultHandles == null) {
                synchronized (InvocationHandlerImpl.class) {
                    if (defaultHandles == null) {
                        defaultHandles = new HashMap<>();
                    }
                    if (defaultHandles == null) {
                        defaultHandles = new HashMap<>();
                    }
                }
            }
        }

        private MethodHandles.Lookup lookup(Method method)  {
            try {
                if (JieSystem.isJava8Higher()) {
                    return MethodHandles.lookup();
                }
                Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class);
                constructor.setAccessible(true);
                return constructor.newInstance(method.getDeclaringClass())
                    .in(method.getDeclaringClass());
            } catch (Exception e) {
                throw new TypeProxyException(e);
            }
        }
    }
}
