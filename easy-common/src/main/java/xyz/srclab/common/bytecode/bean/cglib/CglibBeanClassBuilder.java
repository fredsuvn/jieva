package xyz.srclab.common.bytecode.bean.cglib;

import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.impl.cglib.BeanGenerator;
import xyz.srclab.common.bytecode.impl.cglib.CglibOperator;

import java.util.LinkedList;
import java.util.List;

public class CglibBeanClassBuilder<T> extends CacheStateBuilder<BeanClass<T>> implements BeanClass.Builder<T> {

    public static CglibBeanClassBuilder<Object> newBuilder() {
        return new CglibBeanClassBuilder<>(Object.class);
    }

    public static <T> CglibBeanClassBuilder<T> newBuilder(Class<?> superClass) {
        return new CglibBeanClassBuilder<>(superClass);
    }

    private final Class<?> superClass;
    private final List<PropertyInfo> propertyInfos = new LinkedList<>();

    public CglibBeanClassBuilder(Class<?> superClass) {
        this.superClass = superClass;
    }

    @Override
    public CglibBeanClassBuilder<T> addProperty(String name, Class<?> type) {
        this.propertyInfos.add(new PropertyInfo(name, type));
        return this;
    }

    @Override
    protected BeanClass<T> buildNew() {
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
