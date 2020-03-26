package xyz.srclab.common.bytecode.bean;

import xyz.srclab.common.bytecode.bean.cglib.CglibBeanClassBuilder;

class BeanOperatorLoader {

    private static final BeanOperator INSTANCE = new CglibBeanOperator();

    static BeanOperator getInstance() {
        return INSTANCE;
    }

    static class CglibBeanOperator implements BeanOperator {

        @Override
        public BeanClass.Builder<Object> newBuilder() {
            return CglibBeanClassBuilder.newBuilder();
        }

        @Override
        public <T> BeanClass.Builder<T> newBuilder(Class<T> superClass) {
            return CglibBeanClassBuilder.newBuilder(superClass);
        }
    }
}
