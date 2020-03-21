package xyz.srclab.common.bytecode.impl.cglib.original;

import net.sf.cglib.proxy.*;

public class OriginalCglibOperator implements xyz.srclab.common.bytecode.impl.cglib.CglibOperator {

    @Override
    public xyz.srclab.common.bytecode.impl.cglib.Enhancer newEnhancer() {
        Enhancer actualEnhancer = new Enhancer();
        return new EnhancerImpl(actualEnhancer);
    }

    private static class EnhancerImpl implements xyz.srclab.common.bytecode.impl.cglib.Enhancer {

        private final Enhancer actualEnhancer;

        private EnhancerImpl(Enhancer actualEnhancer) {
            this.actualEnhancer = actualEnhancer;
        }

        @Override
        public void setSuperclass(Class<?> superclass) {
            actualEnhancer.setSuperclass(superclass);
        }

        @Override
        public void setInterfaces(Class<?>[] interfaces) {
            actualEnhancer.setInterfaces(interfaces);
        }

        @Override
        public void setCallbacks(xyz.srclab.common.bytecode.impl.cglib.Callback[] callbacks) {
            Callback[] actualCallbacks = new Callback[callbacks.length];
            for (int i = 0; i < callbacks.length; i++) {
                xyz.srclab.common.bytecode.impl.cglib.Callback callback = callbacks[i];
                if (callback instanceof xyz.srclab.common.bytecode.impl.cglib.NoOp) {
                    actualCallbacks[i] = NoOp.INSTANCE;
                    continue;
                }
                if (callback instanceof xyz.srclab.common.bytecode.impl.cglib.MethodInterceptor) {
                    MethodInterceptor actualMethodInterceptor = toActualMethodInterceptor(
                            (xyz.srclab.common.bytecode.impl.cglib.MethodInterceptor) callback);
                    actualCallbacks[i] = actualMethodInterceptor;
                    continue;
                }
                throw new IllegalArgumentException("Unknown callback: " + callback);
            }
            actualEnhancer.setCallbacks(actualCallbacks);
        }

        private MethodInterceptor toActualMethodInterceptor(
                xyz.srclab.common.bytecode.impl.cglib.MethodInterceptor methodInterceptor) {
            return (object, method, args, proxy) ->
                    methodInterceptor.intercept(object, method, args,
                            new xyz.srclab.common.bytecode.impl.cglib.MethodProxy() {
                                @Override
                                public Object invoke(Object object, Object[] args) throws Throwable {
                                    return proxy.invoke(object, args);
                                }

                                @Override
                                public Object invokeSuper(Object object, Object[] args) throws Throwable {
                                    return proxy.invokeSuper(object, args);
                                }
                            });
        }

        @Override
        public void setCallbackFilter(xyz.srclab.common.bytecode.impl.cglib.CallbackFilter callbackFilter) {
            CallbackFilter actualCallbackFilter = callbackFilter::accept;
            actualEnhancer.setCallbackFilter(actualCallbackFilter);
        }

        @Override
        public Object create() {
            return actualEnhancer.create();
        }

        @Override
        public Object create(Class<?>[] parameterTypes, Object[] args) {
            return actualEnhancer.create(parameterTypes, args);
        }
    }
}
