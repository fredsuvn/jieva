package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface BeanDescriptor {

    Class<?> getType();

    @Nullable
    BeanPropertyDescriptor getPropertyDescriptor(String propertyName);

    Map<String, BeanPropertyDescriptor> getPropertyDescriptors();
}
