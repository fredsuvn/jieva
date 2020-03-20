package xyz.srclab.common.proxy;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.springframework.cglib.proxy.*;
import xyz.srclab.common.exception.ExceptionWrapper;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ClassProxyBuilder<T> {

    public static <T> ClassProxyBuilder<T> newBuilder(Class<T> type) {
        return new ClassProxyBuilder<>(type);
    }

    private final Class<T> type;
    private final LinkedList<
            Pair<Predicate<Method>, Function<MethodContext, Object>>> predicatePairs = new LinkedList<>();

    public ClassProxyBuilder(Class<T> type) {
        this.type = type;
    }

    public ClassProxyBuilder<T> proxyMethod(
            String methodName, Class<?>[] parameterTypes, Function<MethodContext, Object> methodBody) {
        return proxyMethod(
                method -> method.getName().equals(methodName)
                        && Arrays.equals(method.getParameterTypes(), parameterTypes),
                methodBody
        );
    }

    public ClassProxyBuilder<T> proxyMethod(
            Predicate<Method> methodPredicate, Function<MethodContext, Object> methodBody) {
        predicatePairs.addFirst(Pair.of(methodPredicate, methodBody));
        return this;
    }

    public ClassProxy<T> build() {
        Enhancer enhancer = buildEnhancer();
        return new ClassProxy<T>() {
            @Override
            public T newInstance() {
                return (T) enhancer.create();
            }

            @Override
            public T newInstance(Class<?>[] parameterTypes, Object[] arguments) {
                return (T) enhancer.create(parameterTypes, arguments);
            }
        };
    }

    private Enhancer buildEnhancer() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        List<Callback> callbacks = new LinkedList<>();
        Map<Method, Integer> callbackMap = new HashMap<>();
        callbacks.add(NoOp.INSTANCE);
        Method[] methods = type.getMethods();
        int count = 1;
        for (Method typeMethod : methods) {
            Function<MethodContext, Object> function = findMethodProxy(typeMethod);
            if (function == null) {
                continue;
            }
            MethodInterceptor proxy = (object, method, arguments, methodProxy) -> function.apply(new MethodContext() {

                @Override
                public Object getObject() {
                    return object;
                }

                @Override
                public Method getMethod() {
                    return method;
                }

                @Override
                public Object[] getArguments() {
                    return arguments;
                }

                @Override
                public MethodProxy getMethodProxy() {
                    return new MethodProxy() {
                        @Override
                        public Object invoke() {
                            return invoke(getArguments());
                        }

                        @Override
                        public Object invoke(Object[] args) {
                            try {
                                return methodProxy.invokeSuper(object, args);
                            } catch (Throwable throwable) {
                                throw throwable instanceof RuntimeException ?
                                        (RuntimeException) throwable : new ExceptionWrapper(throwable);
                            }
                        }
                    };
                }
            });
            callbacks.add(proxy);
            callbackMap.put(typeMethod, count);
            count++;
        }
        CallbackFilter callbackFilter = method -> {
            Integer index = callbackMap.get(method);
            return index == null ? 0 : index;
        };
        enhancer.setCallbacks(callbacks.toArray(new Callback[0]));
        enhancer.setCallbackFilter(callbackFilter);
        return enhancer;
    }

    @Nullable
    private Function<MethodContext, Object> findMethodProxy(Method method) {
        for (Pair<Predicate<Method>, Function<MethodContext, Object>> predicatePair : predicatePairs) {
            if (predicatePair.getKey().test(method)) {
                return predicatePair.getValue();
            }
        }
        return null;
    }
}
