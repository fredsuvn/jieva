package xyz.srclab.common.bytecode.proxy;

import xyz.srclab.common.bytecode.proxy.cglib.CglibProxyBuilder;

class ProxyOperatorLoader {

    private static final ProxyOperator INSTANCE = new CglibProxyOperator();

    static ProxyOperator getInstance() {
        return INSTANCE;
    }

    static class CglibProxyOperator implements ProxyOperator {

        @Override
        public ProxyBuilder<Object> newBuilder() {
            return CglibProxyBuilder.newBuilder();
        }

        @Override
        public <T> ProxyBuilder<T> newBuilder(Class<T> superClass) {
            return CglibProxyBuilder.newBuilder(superClass);
        }
    }
}
