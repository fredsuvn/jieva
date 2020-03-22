package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.reflect.ReflectHelper;

import java.util.Map;

public interface BeanOperator {

    static Builder newBuilder() {
        return Builder.newBuilder();
    }

    BeanResolver getBeanResolver();

    BeanConverter getBeanConverter();

    BeanOperatorStrategy getBeanOperatorStrategy();

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
        Class<?> type = propertyDescriptor.getType();
        Object typedValue = getBeanConverter().convert(value, type);
        propertyDescriptor.setValue(bean, typedValue);
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
            getBeanOperatorStrategy().getCopyPropertyStrategy().copyProperty(
                    sourcePropertyDescriptor, source, destPropertyDescriptor, dest, this);
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

    class Builder extends CacheStateBuilder<BeanOperator> {

        public static Builder newBuilder() {
            return new Builder();
        }

        private BeanResolver beanResolver;
        private BeanConverter beanConverter;
        private BeanOperatorStrategy beanOperatorStrategy;

        public Builder setBeanResolver(BeanResolver beanResolver) {
            this.changeState();
            this.beanResolver = beanResolver;
            return this;
        }

        public Builder setBeanConverter(BeanConverter beanConverter) {
            this.changeState();
            this.beanConverter = beanConverter;
            return this;
        }

        public Builder setBeanOperatorStrategy(BeanOperatorStrategy beanOperatorStrategy) {
            this.changeState();
            this.beanOperatorStrategy = beanOperatorStrategy;
            return this;
        }

        @Override
        protected BeanOperator buildNew() {
            return new BeanOperatorImpl(this);
        }

        private static class BeanOperatorImpl implements BeanOperator {

            private final BeanResolver beanResolver;
            private final BeanConverter beanConverter;
            private final BeanOperatorStrategy beanOperatorStrategy;

            private BeanOperatorImpl(Builder builder) {
                this.beanResolver = new BeanResolverProxy(builder.beanResolver == null ?
                        CommonBeanResolver.getInstance() : builder.beanResolver);
                this.beanConverter = new BeanConverterProxy(builder.beanConverter == null ?
                        CommonBeanConverter.getInstance() : builder.beanConverter);
                this.beanOperatorStrategy = builder.beanOperatorStrategy == null ?
                        CommonBeanOperatorStrategy.getInstance() : builder.beanOperatorStrategy;
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
            public BeanOperatorStrategy getBeanOperatorStrategy() {
                return beanOperatorStrategy;
            }


            private class BeanResolverProxy implements BeanResolver {

                private final BeanResolver proxied;

                private BeanResolverProxy(BeanResolver proxied) {
                    this.proxied = proxied;
                }

                @Override
                public BeanDescriptor resolve(Object bean) {
                    return resolve(bean, BeanOperatorImpl.this);
                }

                @Override
                public BeanDescriptor resolve(Object bean, BeanOperator beanOperator) {
                    return proxied.resolve(bean, beanOperator);
                }
            }

            private class BeanConverterProxy implements BeanConverter {

                private final BeanConverter proxied;

                private BeanConverterProxy(BeanConverter proxied) {
                    this.proxied = proxied;
                }

                @Nullable
                @Override
                public <T> T convert(@Nullable Object from, Class<T> to) {
                    return convert(from, to, BeanOperatorImpl.this);
                }

                @Nullable
                @Override
                public <T> T convert(@Nullable Object from, Class<T> to, BeanOperator beanOperator) {
                    return proxied.convert(from, to, beanOperator);
                }
            }
        }
    }
}
