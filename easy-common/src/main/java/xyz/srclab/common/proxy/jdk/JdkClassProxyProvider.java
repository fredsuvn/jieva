package xyz.srclab.common.proxy.jdk;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.lang.tuple.Pair;
import xyz.srclab.common.proxy.ClassProxy;
import xyz.srclab.common.proxy.ClassProxyProvider;
import xyz.srclab.common.reflect.method.MethodBody;
import xyz.srclab.common.reflect.method.MethodInvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@ThreadSafe
public class JdkClassProxyProvider implements ClassProxyProvider {

    public static JdkClassProxyProvider getInstance() {
        return INSTANCE;
    }

    private static final JdkClassProxyProvider INSTANCE = new JdkClassProxyProvider();

    @Override
    public <T> ClassProxy.Builder<T> newBuilder(Class<T> type) {
        return new JdkClassProxyBuilder<>(type);
    }

    private static final class JdkClassProxyBuilder<T>
            extends CacheStateBuilder<ClassProxy<T>> implements ClassProxy.Builder<T> {

        private final Class<T> type;
        private final List<Pair<Predicate<Method>, MethodBody<?>>> predicatePairs = new LinkedList<>();

        public JdkClassProxyBuilder(Class<T> type) {
            this.type = type;
        }

        @Override
        public ClassProxy.Builder<T> proxyMethod(Predicate<Method> methodPredicate, MethodBody<?> methodBody) {
            this.changeState();
            predicatePairs.add(Pair.of(methodPredicate, methodBody));
            return this;
        }

        @Override
        protected ClassProxy<T> buildNew() {
            return new JdkClassProxy<>(type, predicatePairs);
        }
    }

    private static final class JdkClassProxy<T> implements ClassProxy<T> {

        private final Class<?> type;
        private final Map<Method, MethodBody<?>> methodMap;

        private JdkClassProxy(Class<?> type, List<Pair<Predicate<Method>, MethodBody<?>>> predicatePairs) {
            this.type = type;
            Map<Method, MethodBody<?>> map = new HashMap<>();
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                for (Pair<Predicate<Method>, MethodBody<?>> predicatePair : predicatePairs) {
                    if (predicatePair.get0().test(method)) {
                        map.put(method, predicatePair.get1());
                    }
                }
            }
            this.methodMap = MapHelper.immutableMap(map);
        }

        @Override
        public T newInstance() {
            Object instance = Proxy.newProxyInstance(getClass().getClassLoader(), type.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    MethodBody<?> methodBody = methodMap.get(method);
                    if (methodBody == null) {
                        return method.invoke(proxy, args);
                    }
                    return methodBody.invoke(proxy, method, args, new MethodInvoker() {
                        @Override
                        public Object invoke(Object object) {
                            try {
                                return method.invoke(object, args);
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException(e);
                            } catch (InvocationTargetException e) {
                                throw new ExceptionWrapper(e);
                            }
                        }

                        @Override
                        public Object invoke(Object object, Object[] args) {
                            try {
                                return method.invoke(object, args);
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException(e);
                            } catch (InvocationTargetException e) {
                                throw new ExceptionWrapper(e);
                            }
                        }
                    });
                }
            });
            return (T) instance;
        }

        @Override
        public T newInstance(Class<?>[] parameterTypes, Object[] arguments) {
            return newInstance();
        }
    }
}
