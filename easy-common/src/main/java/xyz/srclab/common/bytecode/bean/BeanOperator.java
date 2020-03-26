package xyz.srclab.common.bytecode.bean;

interface BeanOperator {

    static BeanOperator getInstance() {
        return BeanOperatorLoader.getInstance();
    }

    BeanClass.Builder<Object> newBuilder();

    <T> BeanClass.Builder<T> newBuilder(Class<T> superClass);
}
