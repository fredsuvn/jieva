package xyz.srclab.common.bean.handlers;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.bean.FsBean;
import xyz.srclab.common.bean.FsBeanException;
import xyz.srclab.common.bean.FsBeanProperty;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.convert.FsConvertException;
import xyz.srclab.common.reflect.FsInvoker;
import xyz.srclab.common.reflect.FsType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Protobuf bean resolve handler.
 * This handler depends on libs about <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 *
 * @author fredsuvn
 */
public class ProtobufResolveHandler implements FsBeanResolver.Handler {

    /**
     * An instance.
     */
    public static final ProtobufResolveHandler INSTANCE = new ProtobufResolveHandler();

    @Override
    public @Nullable Object resolve(FsBeanResolver.BeanBuilder builder) {
        try {
            Class<?> rawType = builder.getRawType();
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
                return Fs.CONTINUE;
            }
            Method getDescriptorMethod = rawType.getMethod("getDescriptor");
            Descriptors.Descriptor descriptor = (Descriptors.Descriptor) getDescriptorMethod.invoke(null);
            for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                FsBeanProperty property = buildProperty(builder, field, rawType, isBuilder);
                builder.getProperties().put(property.getName(), property);
            }
            return Fs.BREAK;
        } catch (FsConvertException e) {
            throw e;
        } catch (Exception e) {
            throw new FsConvertException(e);
        }
    }

    private FsBeanProperty buildProperty(
        FsBeanResolver.BeanBuilder builder,
        Descriptors.FieldDescriptor field,
        Class<?> rawClass,
        boolean isBuilder
    ) throws Exception {

        String rawName = field.getName();

        //map
        if (field.isMapField()) {
            String name = rawName + "Map";
            Method getterMethod = rawClass.getMethod("get" + FsString.capitalize(name));
            Type type = FsType.getGenericSuperType(getterMethod.getGenericReturnType(), Map.class);
            FsInvoker getter = FsInvoker.reflectMethod(getterMethod);
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + FsString.capitalize(rawName));
                Method putAllMethod = rawClass.getMethod("putAll" + FsString.capitalize(rawName), Map.class);
                FsInvoker setter = new FsInvoker() {
                    @Override
                    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
                        try {
                            clearMethod.invoke(inst);
                            return putAllMethod.invoke(inst, args);
                        } catch (InvocationTargetException e) {
                            throw new FsBeanException(e.getCause());
                        } catch (Exception e) {
                            throw new FsBeanException(e);
                        }
                    }
                };
                return new PropertyImpl(name, type, getterMethod, null, getter, setter, builder);
            } else {
                return new PropertyImpl(name, type, getterMethod, null, getter, null, builder);
            }
        }

        //repeated
        if (field.isRepeated()) {
            String name = rawName + "List";
            Method getterMethod = rawClass.getMethod("get" + FsString.capitalize(name));
            Type type = FsType.getGenericSuperType(getterMethod.getGenericReturnType(), List.class);
            FsInvoker getter = FsInvoker.reflectMethod(getterMethod);
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + FsString.capitalize(rawName));
                Method addAllMethod = rawClass.getMethod("addAll" + FsString.capitalize(rawName), Iterable.class);
                FsInvoker setter = new FsInvoker() {
                    @Override
                    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
                        try {
                            clearMethod.invoke(inst);
                            return addAllMethod.invoke(inst, args);
                        } catch (InvocationTargetException e) {
                            throw new FsBeanException(e.getCause());
                        } catch (Exception e) {
                            throw new FsBeanException(e);
                        }
                    }
                };
                return new PropertyImpl(name, type, getterMethod, null, getter, setter, builder);
            } else {
                return new PropertyImpl(name, type, getterMethod, null, getter, null, builder);
            }
        }

        // Simple object
        Method getterMethod = rawClass.getMethod("get" + FsString.capitalize(rawName));
        Type type = getterMethod.getGenericReturnType();
        FsInvoker getter = FsInvoker.reflectMethod(getterMethod);
        if (isBuilder) {
            Method setterMethod = rawClass.getMethod("set" + FsString.capitalize(rawName), FsType.getRawType(type));
            FsInvoker setter = FsInvoker.reflectMethod(setterMethod);
            return new PropertyImpl(rawName, type, getterMethod, setterMethod, getter, setter, builder);
        } else {
            return new PropertyImpl(rawName, type, getterMethod, null, getter, null, builder);
        }
    }

    private static final class PropertyImpl implements FsBeanProperty {

        private final String name;
        private final Type type;
        private final @Nullable Method getterMethod;
        private final @Nullable Method setterMethod;
        private final FsInvoker getter;
        private final @Nullable FsInvoker setter;
        private final FsBean owner;

        private PropertyImpl(
            String name,
            Type type,
            @Nullable Method getterMethod,
            @Nullable Method setterMethod,
            FsInvoker getter,
            @Nullable FsInvoker setter,
            FsBean owner
        ) {
            this.name = name;
            this.type = type;
            this.getterMethod = getterMethod;
            this.setterMethod = setterMethod;
            this.getter = getter;
            this.setter = setter;
            this.owner = owner;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public @Nullable Object get(Object bean) {
            return getter.invoke(bean);
        }

        @Override
        public void set(Object bean, @Nullable Object value) {
            if (setter == null) {
                throw new FsBeanException("Not writeable.");
            }
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
        public FsBean getOwner() {
            return owner;
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
