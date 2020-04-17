package xyz.srclab.common.bytecode.provider.cglib;

import net.sf.cglib.proxy.*;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.lang.Counter;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.bytecode.enhance.EnhancedClass;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.SignatureHelper;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.method.ProxyMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunqian
 */
final class EnhancedClassBuilderImpl<T>
        extends CachedBuilder<EnhancedClass<T>> implements EnhancedClass.Builder<T> {

    private final Enhancer enhancer;
    private final Map<String, ProxyMethod> overrideMethods = new HashMap<>();

    EnhancedClassBuilderImpl(Class<T> superClass) {
        this.enhancer = new Enhancer();
        this.enhancer.setSuperclass(superClass);
    }

    @Override
    public EnhancedClass.Builder<T> addInterfaces(Iterable<Class<?>> interfaces) {
        enhancer.setInterfaces(ArrayHelper.toArray(interfaces, Class.class));
        return this;
    }

    @Override
    public EnhancedClass.Builder<T> overrideMethod(
            String methodName, Class<?>[] parameterTypes, ProxyMethod proxyMethod) {
        overrideMethods.put(
                SignatureHelper.signMethod(methodName, parameterTypes),
                proxyMethod
        );
        return this;
    }

    @Override
    protected EnhancedClass<T> buildNew() {
        Callback[] callbacks = new Callback[overrideMethods.size() + 1];
        callbacks[0] = NoOp.INSTANCE;
        Counter counter = new Counter(1);
        CallbackFilter callbackFilter = method -> {
            String signature = SignatureHelper.signMethod(method);
            if (!overrideMethods.containsKey(signature)) {
                return 0;
            }
            ProxyMethod proxyMethod = overrideMethods.get(signature);
            int index = counter.getIntAndIncrement();
            callbacks[index] = (MethodInterceptor) (instance, method1, args, methodProxy) ->
                    proxyMethod.invoke(index, args, method1, new MethodInvoker() {
                        @Override
                        public @Nullable Object invoke(@Nullable Object object, Object... args) {
                            try {
                                return methodProxy.invokeSuper(object, args);
                            } catch (Throwable throwable) {
                                throw new ExceptionWrapper(throwable);
                            }
                        }
                    });
            return index;
        };
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackFilter(callbackFilter);
        return new EnhancedClass<T>() {
            @Override
            public T newInstance() {
                return (T) enhancer.create();
            }

            @Override
            public T newInstance(Class<?>[] parameterTypes, Object[] args) {
                return (T) enhancer.create(parameterTypes, args);
            }
        };
    }
}
