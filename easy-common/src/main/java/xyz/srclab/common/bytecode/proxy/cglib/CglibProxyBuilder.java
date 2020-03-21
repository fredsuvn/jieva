package xyz.srclab.common.bytecode.proxy.cglib;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.common.bytecode.impl.cglib.*;
import xyz.srclab.common.bytecode.proxy.ProxyBuilder;
import xyz.srclab.common.bytecode.proxy.ProxyClass;
import xyz.srclab.common.collection.CollectionHelper;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.MethodBody;
import xyz.srclab.common.reflect.MethodDefinition;
import xyz.srclab.common.reflect.MethodInvoker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CglibProxyBuilder<T> implements ProxyBuilder<T> {

    public static CglibProxyBuilder<Object> newBuilder() {
        return new CglibProxyBuilder<>(Object.class);
    }

    public static <T> CglibProxyBuilder<T> newBuilder(Class<?> superClass) {
        return new CglibProxyBuilder<>(superClass);
    }

    private final Class<?> superClass;
    private final List<Class<?>> interfaces = new LinkedList<>();
    private final List<MethodInfo> overrideMethods = new LinkedList<>();

    public CglibProxyBuilder(Class<?> superClass) {
        this.superClass = superClass;
    }

    @Override
    public ProxyBuilder<T> addInterfaces(Iterable<Class<?>> interfaces) {
        this.interfaces.addAll(CollectionHelper.castCollection(interfaces));
        return this;
    }

    @Override
    public <R> ProxyBuilder<T> overrideMethod(String name, Class<?>[] parameterTypes, MethodBody<R> methodBody) {
        this.overrideMethods.add(new MethodInfo(name, parameterTypes, methodBody));
        return this;
    }

    @Override
    public <R> ProxyBuilder<T> overrideMethod(MethodDefinition<R> methodDefinition) {
        this.overrideMethods.add(new MethodInfo(
                methodDefinition.getName(), methodDefinition.getParameterTypes(), methodDefinition.getBody()));
        return this;
    }

    @Override
    public ProxyClass<T> build() {
        return new ProxyClassImpl<>(buildEnhancer());
    }

    private Enhancer buildEnhancer() {
        Callback[] callbacks = new Callback[overrideMethods.size() + 1];
        callbacks[0] = NoOp.INSTANCE;
        int[] count = {1};
        CallbackFilter callbackFilter = method -> {
            for (MethodInfo methodInfo : overrideMethods) {
                if (method.getName().equals(methodInfo.getName())
                        && Arrays.deepEquals(method.getParameterTypes(), methodInfo.getParameterTypes())) {
                    MethodInterceptor methodInterceptor = (object, method1, args, proxy) ->
                            methodInfo.getBody().invoke(object, method1, args, new MethodInvoker() {
                                @Override
                                public Object invoke(Object object) {
                                    try {
                                        return proxy.invokeSuper(object, args);
                                    } catch (Throwable throwable) {
                                        throw new ExceptionWrapper(throwable);
                                    }
                                }

                                @Override
                                public Object invoke(Object object, Object[] args) {
                                    try {
                                        return proxy.invokeSuper(object, args);
                                    } catch (Throwable throwable) {
                                        throw new ExceptionWrapper(throwable);
                                    }
                                }
                            });
                    int nowIndex = count[0];
                    callbacks[nowIndex] = methodInterceptor;
                    count[0]++;
                    return nowIndex;
                }
            }
            return 0;
        };

        Enhancer enhancer = CglibOperator.getInstance().newEnhancer();
        enhancer.setSuperclass(superClass);
        enhancer.setInterfaces(interfaces.toArray(ArrayUtils.EMPTY_CLASS_ARRAY));
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackFilter(callbackFilter);
        return enhancer;
    }

    private static class ProxyClassImpl<T> implements ProxyClass<T> {

        private final Enhancer enhancer;

        private ProxyClassImpl(Enhancer enhancer) {
            this.enhancer = enhancer;
        }

        @Override
        public T newInstance() {
            return (T) enhancer.create();
        }

        @Override
        public T newInstance(Class<?>[] parameterTypes, Object[] args) {
            return (T) enhancer.create(parameterTypes, args);
        }
    }
}
