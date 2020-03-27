package xyz.srclab.common.bean;

import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.CacheHelper;
import xyz.srclab.common.exception.ExceptionWrapper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CommonBeanResolverHandler implements BeanResolverHandler {

    public static CommonBeanResolverHandler getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanResolverHandler INSTANCE = new CommonBeanResolverHandler();

    private static final Cache<Class<?>, BeanDescriptor> descriptorCache = CacheHelper.newLocalCache();

    @Override
    public boolean supportBean(Object bean) {
        return !(bean instanceof Map);
    }

    @Override
    public BeanDescriptor resolve(Object bean) {
        return descriptorCache.get(bean.getClass(), type -> {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                return BeanDescriptor.newBuilder()
                        .setType(type)
                        .setProperties(buildProperties(beanInfo.getPropertyDescriptors()))
                        .build();
            } catch (IntrospectionException e) {
                throw new ExceptionWrapper(e);
            }
        });
    }

    private Map<String, BeanPropertyDescriptor> buildProperties(PropertyDescriptor[] descriptors) {
        Map<String, BeanPropertyDescriptor> map = new HashMap<>();
        for (PropertyDescriptor descriptor : descriptors) {
            map.put(descriptor.getName(), new PojoBeanPropertyDescriptor(descriptor));
        }
        return map;
    }

    private static final class PojoBeanPropertyDescriptor implements BeanPropertyDescriptor {

        private final PropertyDescriptor descriptor;
        private final Type genericType;

        private PojoBeanPropertyDescriptor(PropertyDescriptor descriptor) {
            this.descriptor = descriptor;
            this.genericType = findGenericType(descriptor);
        }

        private Type findGenericType(PropertyDescriptor descriptor) {
            Method getter = descriptor.getReadMethod();
            if (getter != null) {
                return getter.getGenericReturnType();
            }
            Method setter = descriptor.getWriteMethod();
            if (setter == null) {
                throw new IllegalStateException("Both getter and setter method are null: " + getName());
            }
            return setter.getGenericParameterTypes()[0];
        }

        @Override
        public String getName() {
            return descriptor.getName();
        }

        @Override
        public Class<?> getType() {
            return descriptor.getPropertyType();
        }

        @Override
        public Type getGenericType() {
            return genericType;
        }

        @Override
        public boolean isReadable() {
            return descriptor.getReadMethod() != null;
        }

        @Override
        public Object getValue(Object bean) throws UnsupportedOperationException {
            if (!isReadable()) {
                throw new UnsupportedOperationException("Property is not readable: " + descriptor);
            }
            Method method = descriptor.getReadMethod();
            try {
                return method.invoke(bean);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new ExceptionWrapper(e.getTargetException());
            }
        }

        @Override
        public boolean isWriteable() {
            return descriptor.getWriteMethod() != null;
        }

        @Override
        public void setValue(Object bean, Object value) throws UnsupportedOperationException {
            if (!isWriteable()) {
                throw new UnsupportedOperationException("Property is not writeable: " + descriptor);
            }
            Method method = descriptor.getWriteMethod();
            try {
                method.invoke(bean, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new ExceptionWrapper(e.getTargetException());
            }
        }
    }
}