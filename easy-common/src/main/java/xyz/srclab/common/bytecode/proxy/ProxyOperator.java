package xyz.srclab.common.bytecode.proxy;

interface ProxyOperator {

    static ProxyOperator getInstance() {
        return ProxyOperatorLoader.getInstance();
    }

    ProxyBuilder<Object> newBuilder();

    <T> ProxyBuilder<T> newBuilder(Class<T> superClass);
}
