package xyz.srclab.common.bytecode.bean;

interface BeanOperator {

    static BeanOperator getInstance() {
        return BeanOperatorLoader.getInstance();
    }

    BeanBuilder<Object> newBuilder();

    <T> BeanBuilder<T> newBuilder(Class<T> superClass);
}
