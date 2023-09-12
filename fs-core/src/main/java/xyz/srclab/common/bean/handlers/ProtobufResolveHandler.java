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
                FsBeanProperty property = buildProperty(field, rawType, isBuilder);
                builder.getProperties().put(property.getName(), property);
            }
            return Fs.CONTINUE;
        } catch (FsConvertException e) {
            throw e;
        } catch (Exception e) {
            throw new FsConvertException(e);
        }
    }

    private FsBeanProperty buildProperty(
        Descriptors.FieldDescriptor field, Class<?> rawClass, boolean isBuilder) throws Exception{
        //return null;
        String rawName = field.getName();

        //map
        if (field.isMapField()) {
            String name = rawName + "Map";
            Method getterMethod = rawClass.getMethod("get" + FsString.capitalize(name));
            Type type = FsType.getGenericSuperType(getterMethod.getGenericReturnType(), Map.class);
            FsInvoker getter = FsInvoker.reflectMethod(getterMethod);
            //getters[name] = GetterInfo(name, type, getter, null, getterMethod)

            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + FsString.capitalize(rawName));
                Method putAllMethod = rawClass.getMethod("putAll"+ FsString.capitalize(rawName), Map.class);
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
                //setters[name] = SetterInfo(name, type, setter, null, null)
            }

            return new PropertyImpl(name, type, getter, null);
        }

        //repeated
//        if (field.isRepeated) {
//            val name = rawName + "List"
//            val getterMethod = rawClass.getMethodOrNull("get${name.capitalize()}")
//            if (getterMethod === null) {
//                return
//            }
//            val type = getterMethod.genericReturnType.getTypeSignature(List::class.java)
//            val getter = getterMethod.toInstInvoke()
//            getters[name] = GetterInfo(name, type, getter, null, getterMethod)
//
//            if (isBuilder) {
//                val clearMethod = rawClass.getMethodOrNull("clear${rawName.capitalize()}")
//                if (clearMethod === null) {
//                    throw IllegalStateException("Cannot find clear method of field: $name")
//                }
//                val addAllMethod =
//                    rawClass.getMethodOrNull("addAll${rawName.capitalize()}", Iterable::class.java)
//                if (addAllMethod === null) {
//                    throw IllegalStateException("Cannot find add-all method of field: $name")
//                }
//                val setter = createSetter(clearMethod, addAllMethod)
//                setters[name] = SetterInfo(name, type, setter, null, null)
//            }
//
//            return
//        }
//
//        // Simple object
//        val getterMethod = rawClass.getMethodOrNull("get${rawName.capitalize()}")
//        if (getterMethod === null) {
//            return
//        }
//        val type = getterMethod.genericReturnType
//        val getter = getterMethod.toInstInvoke()
//        getters[rawName] = GetterInfo(rawName, type, getter, null, getterMethod)
//
//        if (isBuilder) {
//            val setterMethod = rawClass.getMethodOrNull("set${rawName.capitalize()}", type.rawClass)
//            if (setterMethod === null) {
//                throw IllegalStateException("Cannot find setter method of field: $rawName")
//            }
//            val setter = setterMethod.toInstInvoke()
//            setters[rawName] = SetterInfo(rawName, type, setter, null, setterMethod)
//        }
        return null;
    }

    private static final class PropertyImpl implements FsBeanProperty {

        private final String name;
        private final Type type;
        private final FsInvoker getter;
        private final FsInvoker setter;

        private PropertyImpl(String name, Type type, FsInvoker getter, FsInvoker setter) {
            this.name = name;
            this.type = type;
            this.getter = getter;
            this.setter = setter;
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

        }

        @Override
        public Type getType() {
            return null;
        }

        @Override
        public @Nullable Method getGetter() {
            return null;
        }

        @Override
        public @Nullable Method getSetter() {
            return null;
        }

        @Override
        public @Nullable Field getField() {
            return null;
        }

        @Override
        public List<Annotation> getGetterAnnotations() {
            return null;
        }

        @Override
        public List<Annotation> getSetterAnnotations() {
            return null;
        }

        @Override
        public List<Annotation> getFieldAnnotations() {
            return null;
        }

        @Override
        public List<Annotation> getAnnotations() {
            return null;
        }

        @Override
        public FsBean getOwner() {
            return null;
        }

        @Override
        public boolean isReadable() {
            return false;
        }

        @Override
        public boolean isWriteable() {
            return false;
        }
    }
}
