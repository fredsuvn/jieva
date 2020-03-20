package xyz.srclab.common.bean;

import xyz.srclab.common.reflect.ReflectHelper;

import java.util.Map;

public interface BeanOperator {

    static BeanOperatorBuilder newBuilder() {
        return BeanOperatorBuilder.newBuilder();
    }

    BeanResolver getBeanResolver();

    BeanConverter getBeanConverter();

    BeanOperatorStrategy.CopyProperty getCopyPropertyStrategy();

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
}
