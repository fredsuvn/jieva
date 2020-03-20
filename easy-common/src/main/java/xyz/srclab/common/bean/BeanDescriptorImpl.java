package xyz.srclab.common.bean;

import java.util.Collections;
import java.util.Map;

class BeanDescriptorImpl implements BeanDescriptor {

    private final Class<?> type;
    private final Map<String, BeanPropertyDescriptor> properties;

    public BeanDescriptorImpl(Class<?> type, Map<String, BeanPropertyDescriptor> properties) {
        this.type = type;
        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public BeanPropertyDescriptor getPropertyDescriptor(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public Map<String, BeanPropertyDescriptor> getPropertyDescriptors() {
        return properties;
    }
}