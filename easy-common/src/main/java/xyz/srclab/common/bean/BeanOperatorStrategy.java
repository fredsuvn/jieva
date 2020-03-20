package xyz.srclab.common.bean;

public interface BeanOperatorStrategy {

    interface CopyProperty {

        void copyProperty(
                BeanPropertyDescriptor sourceProperty, Object sourceBean,
                BeanPropertyDescriptor destProperty, Object destBean,
                BeanConverter beanConverter);
    }
}
