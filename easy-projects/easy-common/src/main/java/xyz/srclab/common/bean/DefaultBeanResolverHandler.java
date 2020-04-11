package xyz.srclab.common.bean;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.SignatureHelper;
import xyz.srclab.common.reflect.invoke.InvokerHelper;
import xyz.srclab.common.reflect.invoke.MethodInvoker;

import java.beans.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultBeanResolverHandler implements BeanResolverHandler {

    private static final Cache<Class<?>, BeanStruct> CACHE = new ThreadLocalCache<>();

    @Override
    public boolean supportBean(Class<?> beanClass) {
        return true;
    }

    @Override
    public BeanStruct resolve(Class<?> beanClass) {
        return CACHE.getNonNull(beanClass, type -> {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                Method[] methods = type.getMethods();
                return BeanStructSupport.newBuilder()
                        .setType(type)
                        .setProperties(buildProperties(beanInfo.getPropertyDescriptors()))
                        .setMethods(buildMethods(methods))
                        .build();
            } catch (IntrospectionException e) {
                // Don't know how to trigger this exception
                throw new ExceptionWrapper(e);
            }
        });
    }

    private Map<String, BeanProperty> buildProperties(PropertyDescriptor[] descriptors) {
        Map<String, BeanProperty> map = new LinkedHashMap<>();
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor instanceof IndexedPropertyDescriptor) {
                continue;
            }
            map.put(descriptor.getName(), new BeanPropertyImpl(descriptor));
        }
        return map;
    }

    private Map<String, BeanMethod> buildMethods(Method[] methods) {
        Map<String, BeanMethod> map = new LinkedHashMap<>();
        for (Method method : methods) {
            BeanMethod beanMethod = new BeanMethodImpl(method);
            map.put(beanMethod.getSignature(), beanMethod);
        }
        return map;
    }

    private static final class BeanPropertyImpl implements BeanProperty {

        private final PropertyDescriptor descriptor;
        private final Type genericType;

        private final @Nullable Method readMethod;
        private final @Nullable Method writeMethod;
        private final @Nullable MethodInvoker getter;
        private final @Nullable MethodInvoker setter;

        private BeanPropertyImpl(PropertyDescriptor descriptor) {
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
            this.getter = readMethod == null ? null : InvokerHelper.getMethodInvoker(readMethod);
            this.setter = writeMethod == null ? null : InvokerHelper.getMethodInvoker(writeMethod);
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
            return getter != null;
        }

        @Nullable
        @Override
        public Object getValue(Object bean) throws UnsupportedOperationException {
            if (!isReadable()) {
                throw new UnsupportedOperationException("Property is not readable: " + descriptor);
            }
            return getter.invoke(bean);
        }

        @Override
        public @Nullable Method getReadMethod() {
            return readMethod;
        }

        @Override
        public boolean isWriteable() {
            return setter != null;
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException {
            if (!isWriteable()) {
                throw new UnsupportedOperationException("Property is not writeable: " + descriptor);
            }
            setter.invoke(bean, value);
        }

        @Override
        public @Nullable Method getWriteMethod() {
            return writeMethod;
        }

        // Never used:
        //@Override
        //public boolean equals(Object object) {
        //    if (this == object) {
        //        return true;
        //    }
        //    if (object instanceof BeanProperty) {
        //        return Objects.equals(getReadMethod(), ((BeanProperty) object).getReadMethod())
        //                &&
        //                Objects.equals(getWriteMethod(), ((BeanProperty) object).getWriteMethod());
        //    }
        //    return false;
        //}
        //
        //@Override
        //public int hashCode() {
        //    return hashCode;
        //}
    }

    private static final class BeanMethodImpl implements BeanMethod {

        private final Method method;
        private final String signature;
        private final MethodInvoker methodInvoker;

        private BeanMethodImpl(Method method) {
            this.method = method;
            this.signature = SignatureHelper.signMethod(this.method);
            this.methodInvoker = InvokerHelper.getMethodInvoker(this.method);
        }

        @Override
        public String getName() {
            return method.getName();
        }

        @Override
        public String getSignature() {
            return signature;
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
        public @Nullable Object invoke(Object bean, Object... args) {
            return methodInvoker.invoke(bean, args);
        }

        // Never used:
        //@Override
        //public boolean equals(Object object) {
        //    if (this == object) return true;
        //    if (object == null || getClass() != object.getClass()) return false;
        //    BeanMethodImpl that = (BeanMethodImpl) object;
        //    return method.equals(that.method);
        //}
        //
        //@Override
        //public int hashCode() {
        //    return Objects.hash(method);
        //}
    }
}