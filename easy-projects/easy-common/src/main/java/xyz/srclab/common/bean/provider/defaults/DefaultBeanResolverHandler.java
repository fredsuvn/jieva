package xyz.srclab.common.bean.provider.defaults;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Null;
import xyz.srclab.common.bean.BeanClass;
import xyz.srclab.common.bean.BeanMethod;
import xyz.srclab.common.bean.BeanProperty;
import xyz.srclab.common.bean.BeanResolverHandler;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.collection.ListHelper;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.invoke.MethodInvoker;

import java.beans.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

final class DefaultBeanResolverHandler implements BeanResolverHandler {

    static DefaultBeanResolverHandler INSTANCE = new DefaultBeanResolverHandler();

    private static final Cache<Class<?>, BeanClass> cache = Cache.newGcThreadLocalL2();

    @Override
    public boolean supportBean(Class<?> beanClass) {
        return true;
    }

    @Override
    public BeanClass resolve(Class<?> beanClass) {
        return cache.getNonNull(beanClass, type -> {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                Method[] methods = type.getMethods();
                return BeanClass.newBuilder(type)
                        .setProperties(buildProperties(beanClass, beanInfo.getPropertyDescriptors()))
                        .setMethods(buildMethods(methods))
                        .build();
            } catch (IntrospectionException e) {
                // Don't know how to trigger this exception
                throw new ExceptionWrapper(e);
            }
        });
    }

    private Map<String, BeanProperty> buildProperties(Class<?> ownerType, PropertyDescriptor[] descriptors) {
        Map<String, BeanProperty> map = new LinkedHashMap<>();
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor instanceof IndexedPropertyDescriptor) {
                continue;
            }
            map.put(descriptor.getName(), new BeanPropertyImpl(ownerType, descriptor));
        }
        return map;
    }

    private Map<Method, BeanMethod> buildMethods(Method[] methods) {
        Map<Method, BeanMethod> map = new LinkedHashMap<>();
        for (Method method : methods) {
            BeanMethod beanMethod = new BeanMethodImpl(method);
            map.put(method, beanMethod);
        }
        return map;
    }

    private static final class BeanPropertyImpl implements BeanProperty {

        private final Class<?> ownerType;
        private final PropertyDescriptor descriptor;
        private final Type genericType;

        private final @Nullable Method readMethod;
        private final @Nullable Method writeMethod;

        private @Nullable MethodInvoker getter;
        private @Nullable MethodInvoker setter;
        private @Nullable Field field;
        private @Nullable @Immutable List<Annotation> annotations;

        private BeanPropertyImpl(Class<?> ownerType, PropertyDescriptor descriptor) {
            this.ownerType = ownerType;
            this.descriptor = descriptor;
            this.readMethod = descriptor.getReadMethod();
            this.writeMethod = descriptor.getWriteMethod();
            if (readMethod == null && writeMethod == null) {
                // Should never reached:
                throw new IllegalStateException("Both getter and setter method are null: " + getName());
            }
            this.genericType = readMethod == null ?
                    writeMethod.getGenericParameterTypes()[0]
                    :
                    readMethod.getGenericReturnType();
        }

        @Override
        public String getName() {
            return descriptor.getName();
        }

        @Override
        public Class<?> getOwnerType() {
            return ownerType;
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
            return readMethod != null;
        }

        @Nullable
        @Override
        public Object getValue(Object bean) throws UnsupportedOperationException {
            if (!isReadable()) {
                throw new UnsupportedOperationException("Property is not readable: " + descriptor);
            }
            if (getter == null) {
                getter = MethodInvoker.of(readMethod);
            }
            return getter.invoke(bean);
        }

        @Override
        public boolean isWriteable() {
            return writeMethod != null;
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException {
            if (!isWriteable()) {
                throw new UnsupportedOperationException("Property is not writeable: " + descriptor);
            }
            if (setter == null) {
                setter = MethodInvoker.of(writeMethod);
            }
            setter.invoke(bean, value);
        }

        @Override
        public @Nullable Field getField() {
            if (field == null) {
                try {
                    field = ownerType.getDeclaredField(getName());
                } catch (NoSuchFieldException e) {
                    field = Null.asField();
                }
            }
            return Null.isNull(field) ? null : field;
        }

        @Override
        public @Immutable List<Annotation> getFieldAnnotations() {
            if (annotations == null) {
                @Nullable Field field = getField();
                if (field == null) {
                    annotations = Collections.emptyList();
                } else {
                    annotations = ListHelper.immutable(Arrays.asList(field.getAnnotations()));
                }
            }
            return annotations;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof BeanPropertyImpl) {
                return descriptor.equals(((BeanPropertyImpl) object).descriptor);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return descriptor.hashCode();
        }
    }

    private static final class BeanMethodImpl implements BeanMethod {

        private final Method method;
        private @Nullable MethodInvoker methodInvoker;

        private BeanMethodImpl(Method method) {
            this.method = method;
        }

        @Override
        public String getName() {
            return method.getName();
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return method.getParameterTypes();
        }

        @Override
        public Type[] getGenericParameterTypes() {
            return method.getGenericParameterTypes();
        }

        @Override
        public int getParameterCount() {
            return method.getParameterCount();
        }

        @Override
        public Class<?> getReturnType() {
            return method.getReturnType();
        }

        @Override
        public Type getGenericReturnType() {
            return method.getGenericReturnType();
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public MethodInvoker getMethodInvoker() {
            if (methodInvoker == null) {
                methodInvoker = MethodInvoker.of(method);
            }
            return methodInvoker;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof BeanMethodImpl) {
                return method.equals(((BeanMethodImpl) object).method);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return method.hashCode();
        }
    }
}