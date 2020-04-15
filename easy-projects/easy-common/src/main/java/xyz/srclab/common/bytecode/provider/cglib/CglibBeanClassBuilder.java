package xyz.srclab.common.bytecode.provider.cglib;

import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.bytecode.bean.BeanClass;

import java.util.LinkedList;
import java.util.List;

final class CglibBeanClassBuilder<T> extends CacheStateBuilder<BeanClass<T>> implements BeanClass.Builder<T> {

    private final CglibAdaptor cglibAdaptor;
    private final Class<?> superClass;
    private final List<PropertyInfo> propertyInfos = new LinkedList<>();

    CglibBeanClassBuilder(CglibAdaptor cglibAdaptor, Class<?> superClass) {
        this.cglibAdaptor = cglibAdaptor;
        this.superClass = superClass;
    }

    @Override
    public CglibBeanClassBuilder<T> addProperty(String name, Class<?> type) {
        this.changeState();
        this.propertyInfos.add(new PropertyInfo(name, type));
        return this;
    }

    @Override
    protected BeanClass<T> buildNew() {
        return new BeanClassImpl<T>(buildBeanGenerator());
    }

    private BeanGenerator buildBeanGenerator() {
        BeanGenerator beanGenerator = cglibAdaptor.newBeanGenerator();
        beanGenerator.setSuperclass(superClass);
        for (PropertyInfo propertyInfo : propertyInfos) {
            beanGenerator.addProperty(propertyInfo.getName(), propertyInfo.getType());
        }
        return beanGenerator;
    }

    private static final class BeanClassImpl<T> implements BeanClass<T> {

        private final BeanGenerator beanGenerator;

        private BeanClassImpl(BeanGenerator beanGenerator) {
            this.beanGenerator = beanGenerator;
        }

        @Override
        public T newInstance() {
            return (T) beanGenerator.create();
        }
    }

    private static final class PropertyInfo {

        private final String name;
        private final Class<?> type;

        PropertyInfo(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }
    }
}
