package xyz.srclab.common.bytecode.bean.cglib;

import xyz.srclab.common.bytecode.bean.BeanBuilder;
import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.impl.cglib.BeanGenerator;
import xyz.srclab.common.bytecode.impl.cglib.CglibOperator;

import java.util.LinkedList;
import java.util.List;

public class CglibBeanBuilder<T> implements BeanBuilder<T> {

    public static CglibBeanBuilder<Object> newBuilder() {
        return new CglibBeanBuilder<>(Object.class);
    }

    public static <T> CglibBeanBuilder<T> newBuilder(Class<?> superClass) {
        return new CglibBeanBuilder<>(superClass);
    }

    private final Class<?> superClass;
    private final List<PropertyInfo> propertyInfos = new LinkedList<>();

    public CglibBeanBuilder(Class<?> superClass) {
        this.superClass = superClass;
    }

    @Override
    public BeanBuilder<T> addProperty(String name, Class<?> type) {
        this.propertyInfos.add(new PropertyInfo(name, type));
        return this;
    }

    @Override
    public BeanClass<T> build() {
        return new BeanClassImpl<T>(buildBeanGenerator());
    }

    private BeanGenerator buildBeanGenerator() {
        BeanGenerator beanGenerator = CglibOperator.getInstance().newBeanGenerator();
        beanGenerator.setSuperclass(superClass);
        for (PropertyInfo propertyInfo : propertyInfos) {
            beanGenerator.addProperty(propertyInfo.getName(), propertyInfo.getType());
        }
        return beanGenerator;
    }

    private static class BeanClassImpl<T> implements BeanClass<T> {

        private final BeanGenerator beanGenerator;

        private BeanClassImpl(BeanGenerator beanGenerator) {
            this.beanGenerator = beanGenerator;
        }

        @Override
        public T newInstance() {
            return (T) beanGenerator.create();
        }
    }
}
