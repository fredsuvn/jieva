package xyz.srclab.common.bean;

import xyz.srclab.common.reflect.ReflectHelper;

import java.util.Map;

public interface BeanOperator {

    static Builder newBuilder() {
        return Builder.newBuilder();
    }

    BeanResolver getBeanResolver();

    BeanConverter getBeanConverter();

    BeanOperatorStrategy.CopyProperty getCopyPropertyStrategy();

    default Object getProperty(Object bean, String propertyName) {
        BeanDescriptor beanDescriptor = getBeanResolver().resolve(bean);
        BeanPropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(propertyName);
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("Cannot find property: " + propertyName);
        }
        return propertyDescriptor.getValue(bean);
    }

    default <T> T getProperty(Object bean, String propertyName, Class<T> type) {
        Object property = getProperty(bean, propertyName);
        return getBeanConverter().convert(property, type);
    }

    default void setProperty(Object bean, String propertyName, Object value) {
        BeanDescriptor beanDescriptor = getBeanResolver().resolve(bean);
        BeanPropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(propertyName);
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("Cannot find property: " + propertyName);
        }
        propertyDescriptor.setValue(bean, value);
    }

    default void copyProperties(Object source, Object dest) {
        BeanDescriptor sourceDescriptor = getBeanResolver().resolve(source);
        BeanDescriptor destDescriptor = getBeanResolver().resolve(dest);
        Map<String, BeanPropertyDescriptor> sourcePropertyDescriptorMap = sourceDescriptor.getPropertyDescriptors();
        sourcePropertyDescriptorMap.forEach((name, sourcePropertyDescriptor) -> {
            BeanPropertyDescriptor destPropertyDescriptor = destDescriptor.getPropertyDescriptor(name);
            if (destPropertyDescriptor == null) {
                return;
            }
            getCopyPropertyStrategy().copyProperty(
                    sourcePropertyDescriptor, source, destPropertyDescriptor, dest, getBeanConverter());
        });
    }

    default <T> T convert(Object from, Class<T> to) {
        return getBeanConverter().convert(from, to);
    }

    default <T> T copy(T from) {
        T returned = (T) ReflectHelper.newInstance(from.getClass());
        copyProperties(from, returned);
        return returned;
    }

    class Builder {

        public static Builder newBuilder() {
            return new Builder();
        }

        private BeanResolver beanResolver = CommonBeanResolver.getInstance();
        private BeanConverter beanConverter = CommonBeanConverter.getInstance();
        private BeanOperatorStrategy.CopyProperty copyPropertyStrategy =
                CommonBeanOperatorStrategy.CopyProperty.getInstance();

        public Builder setBeanResolver(BeanResolver beanResolver) {
            this.beanResolver = beanResolver;
            return this;
        }

        public Builder setBeanConverter(BeanConverter beanConverter) {
            this.beanConverter = beanConverter;
            return this;
        }

        public Builder setCopyPropertyStrategy(BeanOperatorStrategy.CopyProperty copyPropertyStrategy) {
            this.copyPropertyStrategy = copyPropertyStrategy;
            return this;
        }

        public BeanOperator build() {
            return new BeanOperatorImpl(this);
        }

        private static class BeanOperatorImpl implements BeanOperator {

            private final BeanResolver beanResolver;
            private final BeanConverter beanConverter;
            private final BeanOperatorStrategy.CopyProperty copyPropertyStrategy;

            private BeanOperatorImpl(Builder builder) {
                this.beanResolver = builder.beanResolver;
                this.beanConverter = builder.beanConverter;
                this.copyPropertyStrategy = builder.copyPropertyStrategy;
            }

            @Override
            public BeanResolver getBeanResolver() {
                return beanResolver;
            }

            @Override
            public BeanConverter getBeanConverter() {
                return beanConverter;
            }

            @Override
            public BeanOperatorStrategy.CopyProperty getCopyPropertyStrategy() {
                return copyPropertyStrategy;
            }
        }
    }
}
