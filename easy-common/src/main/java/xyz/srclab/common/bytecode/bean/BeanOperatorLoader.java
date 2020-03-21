package xyz.srclab.common.bytecode.bean;

import xyz.srclab.common.bytecode.bean.cglib.CglibBeanBuilder;

class BeanOperatorLoader {

    private static final BeanOperator INSTANCE = new CglibBeanOperator();

    static BeanOperator getInstance() {
        return INSTANCE;
    }

    static class CglibBeanOperator implements BeanOperator {

        @Override
        public BeanBuilder<Object> newBuilder() {
            return CglibBeanBuilder.newBuilder();
        }

        @Override
        public <T> BeanBuilder<T> newBuilder(Class<T> superClass) {
            return CglibBeanBuilder.newBuilder(superClass);
        }
    }
}
