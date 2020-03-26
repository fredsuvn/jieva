package xyz.srclab.common.bytecode.proxy;

interface ProxyOperator {

    static ProxyOperator getInstance() {
        return ProxyOperatorLoader.getInstance();
    }

    ProxyClass.Builder<Object> newBuilder();

    <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass);
}
