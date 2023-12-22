package xyz.fsgek.common.data.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekFlag;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.bean.GekBeanException;
import xyz.fsgek.common.bean.GekBeanResolver;
import xyz.fsgek.common.bean.GekPropertyBase;
import xyz.fsgek.common.convert.GekConvertException;
import xyz.fsgek.common.invoke.GekInvoker;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Resolve handler implementation for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * This handler depends on protobuf libs in the runtime.
 *
 * @author fredsuvn
 */
public class ProtobufResolveHandler implements GekBeanResolver.Handler {

    /**
     * An instance.
     */
    public static final ProtobufResolveHandler INSTANCE = new ProtobufResolveHandler();

    @Override
    public @Nullable GekFlag resolve(GekBeanResolver.ResolveContext context) {
        try {
            Class<?> rawType = GekReflect.getRawType(context.beanType());
            if (rawType == null) {
                return null;
            }
            //Check whether it is a protobuf object
            boolean isProtobuf = false;
            boolean isBuilder = false;
            if (Message.class.isAssignableFrom(rawType)) {
                isProtobuf = true;
            }
            if (Message.Builder.class.isAssignableFrom(rawType)) {
                isProtobuf = true;
                isBuilder = true;
            }
            if (!isProtobuf) {
                return null;
            }
            Method getDescriptorMethod = rawType.getMethod("getDescriptor");
            Descriptors.Descriptor descriptor = (Descriptors.Descriptor) getDescriptorMethod.invoke(null);
            for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                GekPropertyBase propBase = buildProperty(context, field, rawType, isBuilder);
                context.beanProperties().put(propBase.getName(), propBase);
            }
            return GekFlag.BREAK;
        } catch (GekConvertException e) {
            throw e;
        } catch (Exception e) {
            throw new GekConvertException(e);
        }
    }

    private GekPropertyBase buildProperty(
        GekBeanResolver.ResolveContext builder,
        Descriptors.FieldDescriptor field,
        Class<?> rawClass,
        boolean isBuilder
    ) throws Exception {

        String rawName = field.getName();

        //map
        if (field.isMapField()) {
            String name = rawName + "Map";
            Method getterMethod = rawClass.getMethod("get" + GekString.capitalize(name));
            Type type = GekReflect.getGenericSuperType(getterMethod.getGenericReturnType(), Map.class);
            GekInvoker getter = GekInvoker.reflectMethod(getterMethod);
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + GekString.capitalize(rawName));
                Method putAllMethod = rawClass.getMethod("putAll" + GekString.capitalize(rawName), Map.class);
                GekInvoker setter = new GekInvoker() {
                    @Override
                    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
                        try {
                            clearMethod.invoke(inst);
                            return putAllMethod.invoke(inst, args);
                        } catch (InvocationTargetException e) {
                            throw new GekBeanException(e.getCause());
                        } catch (Exception e) {
                            throw new GekBeanException(e);
                        }
                    }
                };
                return new PropertyImpl(name, type, getterMethod, null, getter, setter);
            } else {
                return new PropertyImpl(name, type, getterMethod, null, getter, null);
            }
        }

        //repeated
        if (field.isRepeated()) {
            String name = rawName + "List";
            Method getterMethod = rawClass.getMethod("get" + GekString.capitalize(name));
            Type type = GekReflect.getGenericSuperType(getterMethod.getGenericReturnType(), List.class);
            GekInvoker getter = GekInvoker.reflectMethod(getterMethod);
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + GekString.capitalize(rawName));
                Method addAllMethod = rawClass.getMethod("addAll" + GekString.capitalize(rawName), Iterable.class);
                GekInvoker setter = new GekInvoker() {
                    @Override
                    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
                        try {
                            clearMethod.invoke(inst);
                            return addAllMethod.invoke(inst, args);
                        } catch (InvocationTargetException e) {
                            throw new GekBeanException(e.getCause());
                        } catch (Exception e) {
                            throw new GekBeanException(e);
                        }
                    }
                };
                return new PropertyImpl(name, type, getterMethod, null, getter, setter);
            } else {
                return new PropertyImpl(name, type, getterMethod, null, getter, null);
            }
        }

        // Simple object
        Method getterMethod = rawClass.getMethod("get" + GekString.capitalize(rawName));
        Type type = getterMethod.getGenericReturnType();
        GekInvoker getter = GekInvoker.reflectMethod(getterMethod);
        if (isBuilder) {
            Method setterMethod = rawClass.getMethod("set" + GekString.capitalize(rawName), GekReflect.getRawType(type));
            GekInvoker setter = GekInvoker.reflectMethod(setterMethod);
            return new PropertyImpl(rawName, type, getterMethod, setterMethod, getter, setter);
        } else {
            return new PropertyImpl(rawName, type, getterMethod, null, getter, null);
        }
    }

    private static final class PropertyImpl implements GekPropertyBase {

        private final String name;
        private final Type type;
        private final @Nullable Method getterMethod;
        private final @Nullable Method setterMethod;
        private final GekInvoker getter;
        private final @Nullable GekInvoker setter;

        private PropertyImpl(
            String name,
            Type type,
            @Nullable Method getterMethod,
            @Nullable Method setterMethod,
            GekInvoker getter,
            @Nullable GekInvoker setter
        ) {
            this.name = name;
            this.type = type;
            this.getterMethod = getterMethod;
            this.setterMethod = setterMethod;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public @Nullable Object getValue(Object bean) {
            return getter.invoke(bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) {
            if (setter == null) {
                throw new GekBeanException("Not writeable.");
            }
            setter.invoke(bean, value);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public @Nullable Method getGetter() {
            return getterMethod;
        }

        @Override
        public @Nullable Method getSetter() {
            return setterMethod;
        }

        @Override
        public @Nullable Field getField() {
            return null;
        }

        @Override
        public List<Annotation> getGetterAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getSetterAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getFieldAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWriteable() {
            return setter != null;
        }
    }
}
