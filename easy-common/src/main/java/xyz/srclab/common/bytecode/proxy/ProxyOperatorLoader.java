package xyz.srclab.common.bytecode.proxy;

import xyz.srclab.common.bytecode.proxy.cglib.CglibProxyClassBuilder;

class ProxyOperatorLoader {

    private static final ProxyOperator INSTANCE = new CglibProxyOperator();

    static ProxyOperator getInstance() {
        return INSTANCE;
    }

    static class CglibProxyOperator implements ProxyOperator {

        @Override
        public ProxyClass.Builder<Object> newBuilder() {
            return CglibProxyClassBuilder.newBuilder();
        }

        @Override
        public <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass) {
            return CglibProxyClassBuilder.newBuilder(superClass);
        }
    }
}
