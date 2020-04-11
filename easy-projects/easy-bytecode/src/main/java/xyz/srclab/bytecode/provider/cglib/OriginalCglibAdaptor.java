package xyz.srclab.bytecode.provider.cglib;

import net.sf.cglib.beans.BeanGenerator;

final class OriginalCglibAdaptor implements CglibAdaptor {

    @Override
    public xyz.srclab.bytecode.provider.cglib.Enhancer newEnhancer() {
        net.sf.cglib.proxy.Enhancer actualEnhancer = new net.sf.cglib.proxy.Enhancer();
        return new EnhancerImpl(actualEnhancer);
    }

    @Override
    public xyz.srclab.bytecode.provider.cglib.BeanGenerator newBeanGenerator() {
        BeanGenerator actualBeanGenerator = new BeanGenerator();
        return new BeanGeneratorImpl(actualBeanGenerator);
    }

    private static final class EnhancerImpl implements xyz.srclab.bytecode.provider.cglib.Enhancer {

        private final net.sf.cglib.proxy.Enhancer actualEnhancer;

        private EnhancerImpl(net.sf.cglib.proxy.Enhancer actualEnhancer) {
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
        public void setCallbacks(xyz.srclab.bytecode.provider.cglib.Callback[] callbacks) {
            net.sf.cglib.proxy.Callback[] actualCallbacks = new net.sf.cglib.proxy.Callback[callbacks.length];
            for (int i = 0; i < callbacks.length; i++) {
                xyz.srclab.bytecode.provider.cglib.Callback callback = callbacks[i];
                if (callback instanceof xyz.srclab.bytecode.provider.cglib.NoOp) {
                    actualCallbacks[i] = net.sf.cglib.proxy.NoOp.INSTANCE;
                    continue;
                }
                if (callback instanceof xyz.srclab.bytecode.provider.cglib.MethodInterceptor) {
                    net.sf.cglib.proxy.MethodInterceptor actualMethodInterceptor = toActualMethodInterceptor(
                            (xyz.srclab.bytecode.provider.cglib.MethodInterceptor) callback);
                    actualCallbacks[i] = actualMethodInterceptor;
                    continue;
                }
                throw new IllegalArgumentException("Unknown callback: " + callback);
            }
            actualEnhancer.setCallbacks(actualCallbacks);
        }

        private net.sf.cglib.proxy.MethodInterceptor toActualMethodInterceptor(
                xyz.srclab.bytecode.provider.cglib.MethodInterceptor methodInterceptor) {
            return (object, method, args, proxy) ->
                    methodInterceptor.intercept(object, method, args,
                            new xyz.srclab.bytecode.provider.cglib.MethodProxy() {
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
        public void setCallbackFilter(xyz.srclab.bytecode.provider.cglib.CallbackFilter callbackFilter) {
            net.sf.cglib.proxy.CallbackFilter actualCallbackFilter = callbackFilter::accept;
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

    private static final class BeanGeneratorImpl implements xyz.srclab.bytecode.provider.cglib.BeanGenerator {

        private final BeanGenerator actualBeanGenerator;

        private BeanGeneratorImpl(BeanGenerator actualBeanGenerator) {
            this.actualBeanGenerator = actualBeanGenerator;
        }

        @Override
        public void setSuperclass(Class<?> superclass) {
            actualBeanGenerator.setSuperclass(superclass);
        }

        @Override
        public void addProperty(String name, Class<?> type) {
            actualBeanGenerator.addProperty(name, type);
        }

        @Override
        public Object create() {
            return actualBeanGenerator.create();
        }
    }
}
