package xyz.srclab.common.bean.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsCase;
import xyz.srclab.common.base.FsFinal;
import xyz.srclab.common.bean.FsBean;
import xyz.srclab.common.bean.FsBeanException;
import xyz.srclab.common.bean.FsBeanProperty;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.FsInvoker;
import xyz.srclab.common.reflect.FsType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Default bean resolve handler for {@link FsBeanResolver}.
 *
 * @author fredsuvn
 */
public class DefaultBeanResolveHandler implements FsBeanResolver.Handler {

    /**
     * Reflect mode: using reflect to invoke getter and setter methods.
     */
    public static final int REFLECT_MODE = 1;
    /**
     * Un-reflect mode: using un-reflect ({@link java.lang.invoke.MethodHandles})
     * to invoke getter and setter methods.
     */
    public static final int UNREFLECT_MODE = 2;
    /**
     * Bean naming mapping: starting with "get"/"set" for each getter and setter methods.
     */
    public static final int BEAN_NAMING_MAPPING = 1;
    /**
     * Record naming mapping: Directly using property name for getter methods,
     * and starting with "set" for setter methods.
     */
    public static final int RECORD_NAMING_MAPPING = 2;

    private final int invokeMode;
    private final int propertyNaming;
    private final FsCase propertyNamingCase;
    private final FsCase methodNamingCase;

    /**
     * Construct with options:
     * <ul>
     *     <li>
     *         Invoke mode: {@link #REFLECT_MODE};
     *     </li>
     *     <li>
     *         Property name mapping: {@link #BEAN_NAMING_MAPPING};
     *     </li>
     *     <li>
     *         Property/Method naming case: {@link FsCase#LOWER_CAMEL};
     *     </li>
     * </ul>
     */
    public DefaultBeanResolveHandler() {
        this(REFLECT_MODE, BEAN_NAMING_MAPPING, FsCase.LOWER_CAMEL, FsCase.LOWER_CAMEL);
    }

    /**
     * Constructs with options:
     * <ul>
     *     <li>
     *         Invoke mode: {@link #REFLECT_MODE}, {@link #UNREFLECT_MODE};
     *     </li>
     *     <li>
     *         Property name mapping: {@link #BEAN_NAMING_MAPPING}, {@link #RECORD_NAMING_MAPPING};
     *     </li>
     *     <li>
     *         Property/Method naming case: naming case for property and methods.
     *     </li>
     * </ul>
     *
     * @param invokeMode         invoke mode
     * @param propertyNaming     property name mapping
     * @param propertyNamingCase property naming case
     * @param methodNamingCase   method naming case
     */
    public DefaultBeanResolveHandler(
        int invokeMode, int propertyNaming, FsCase propertyNamingCase, FsCase methodNamingCase) {
        this.invokeMode = invokeMode;
        this.propertyNaming = propertyNaming;
        this.propertyNamingCase = propertyNamingCase;
        this.methodNamingCase = methodNamingCase;
    }

    @Override
    public @Nullable Object resolve(BeanBuilder builder) {
        Type type = builder.getType();
        Class<?> rawType = FsType.getRawType(type);
        if (rawType == null) {
            throw new IllegalArgumentException("The type to be resolved must be Class or ParameterizedType.");
        }
        Method[] methods = rawType.getMethods();
        Map<TypeVariable<?>, Type> typeParameterMapping = FsType.getTypeParameterMapping(type);
        Map<String, Method> getters = new LinkedHashMap<>();
        Map<String, Method> setters = new LinkedHashMap<>();
        for (Method method : methods) {
            if (method.isBridge()) {
                continue;
            }
            String propertyName = isGetter(method);
            if (propertyName != null) {
                getters.put(propertyName, method);
                continue;
            }
            propertyName = isSetter(method);
            if (propertyName != null) {
                setters.put(propertyName, method);
            }
        }
        Set<Type> stack = new HashSet<>();
        getters.forEach((name, getter) -> {
            Method setter = setters.get(name);
            // Type of getter and setter must be same.
            // But field's type will not be checked,
            // because field is only a name holder for adding Annotations conveniently.
            Type returnType = getter.getGenericReturnType();
            returnType = getActualType(returnType, typeParameterMapping, stack);
            if (setter != null) {
                Type setType = setter.getGenericParameterTypes()[0];
                setType = getActualType(setType, typeParameterMapping, stack);
                if (!Objects.equals(returnType, setType)) {
                    return;
                }
            }
            Field field = findField(name, rawType);
            builder.getProperties().put(name, new DefaultFsBeanPropertyImpl(builder, name, getter, setter, field, returnType));
            setters.remove(name);
        });
        setters.forEach((name, setter) -> {
            Field field = findField(name, rawType);
            Type setType = setter.getGenericParameterTypes()[0];
            setType = getActualType(setType, typeParameterMapping, stack);
            builder.getProperties().put(name, new DefaultFsBeanPropertyImpl(builder, name, null, setter, field, setType));
        });
        return Fs.CONTINUE;
    }

    @Nullable
    private String isGetter(Method method) {
        switch (propertyNaming) {
            case BEAN_NAMING_MAPPING:
                if (method.getParameterCount() == 0
                    && method.getName().length() > 3
                    && method.getName().startsWith("get")
                ) {
                    List<CharSequence> words = methodNamingCase.split(method.getName());
                    if (FsCollect.isNotEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "get")) {
                        return propertyNamingCase.join(words.subList(1, words.size()));
                    }
                }
                return null;
            case RECORD_NAMING_MAPPING:
                if (method.getParameterCount() == 0) {
                    return method.getName();
                }
                return null;
            default:
                throw new IllegalArgumentException("Unknown property naming: " + propertyNaming + ".");
        }
    }

    @Nullable
    private String isSetter(Method method) {
        switch (propertyNaming) {
            case BEAN_NAMING_MAPPING:
            case RECORD_NAMING_MAPPING:
                if (method.getParameterCount() == 1
                    && method.getName().length() > 3
                    && method.getName().startsWith("set")
                ) {
                    List<CharSequence> words = methodNamingCase.split(method.getName());
                    if (FsCollect.isNotEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "set")) {
                        return propertyNamingCase.join(words.subList(1, words.size()));
                    }
                }
                return null;
            default:
                throw new IllegalArgumentException("Unknown property naming: " + propertyNaming + ".");
        }
    }

    private Type getActualType(Type type, Map<TypeVariable<?>, Type> typeParameterMapping, Set<Type> stack) {
        if (type instanceof Class) {
            return type;
        }
        stack.clear();
        Type result = FsCollect.getNested(typeParameterMapping, type, stack);
        if (result == null) {
            return type;
        }
        return result;
    }

    @Nullable
    private Field findField(String name, Class<?> type) {
        try {
            return type.getField(name);
        } catch (NoSuchFieldException e) {
            Class<?> cur = type;
            while (cur != null) {
                try {
                    return cur.getDeclaredField(name);
                } catch (NoSuchFieldException ex) {
                    cur = cur.getSuperclass();
                }
            }
        }
        return null;
    }

    private final class DefaultFsBeanPropertyImpl extends FsFinal implements FsBeanProperty {

        private final FsBean owner;
        private final String name;
        private final Method getter;
        private final Method setter;
        private final Field field;
        private final Type type;
        private final FsInvoker getterInvoker;
        private final FsInvoker setterInvoker;
        private final List<Annotation> getterAnnotations;
        private final List<Annotation> setterAnnotations;
        private final List<Annotation> fieldAnnotations;
        private final List<Annotation> allAnnotations;

        private DefaultFsBeanPropertyImpl(
            FsBean owner, String name,
            @Nullable Method getter, @Nullable Method setter,
            @Nullable Field field, Type type
        ) {
            this.owner = owner;
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.field = field;
            this.type = type;
            switch (invokeMode) {
                case REFLECT_MODE:
                    if (getter != null) {
                        getterInvoker = FsInvoker.reflectMethod(getter);
                    } else {
                        getterInvoker = null;
                    }
                    if (setter != null) {
                        setterInvoker = FsInvoker.reflectMethod(setter);
                    } else {
                        setterInvoker = null;
                    }
                    break;
                case UNREFLECT_MODE:
                    if (getter != null) {
                        getterInvoker = FsInvoker.unreflectMethod(getter);
                    } else {
                        getterInvoker = null;
                    }
                    if (setter != null) {
                        setterInvoker = FsInvoker.unreflectMethod(setter);
                    } else {
                        setterInvoker = null;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown invoke mode setting: " + invokeMode + ".");
            }
            getterAnnotations = getter == null ? Collections.emptyList() :
                Collections.unmodifiableList(Arrays.asList(getter.getAnnotations()));
            setterAnnotations = setter == null ? Collections.emptyList() :
                Collections.unmodifiableList(Arrays.asList(setter.getAnnotations()));
            fieldAnnotations = field == null ? Collections.emptyList() :
                Collections.unmodifiableList(Arrays.asList(field.getAnnotations()));
            List<Annotation> annotations = new ArrayList<>(
                getterAnnotations.size() + setterAnnotations.size() + fieldAnnotations.size());
            annotations.addAll(getterAnnotations);
            annotations.addAll(setterAnnotations);
            annotations.addAll(fieldAnnotations);
            allAnnotations = Collections.unmodifiableList(annotations);
        }

        @Override
        public @Nullable Object get(Object bean) {
            if (getterInvoker == null) {
                throw new FsBeanException("Property is not readable: " + name + ".");
            }
            return getterInvoker.invoke(bean);
        }

        @Override
        public void set(Object bean, @Nullable Object value) {
            if (setterInvoker == null) {
                throw new FsBeanException("Property is not writeable: " + name + ".");
            }
            setterInvoker.invoke(bean, value);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public @Nullable Method getGetter() {
            return getter;
        }

        @Override
        public @Nullable Method getSetter() {
            return setter;
        }

        @Override
        public @Nullable Field getField() {
            return field;
        }

        @Override
        public List<Annotation> getGetterAnnotations() {
            return getterAnnotations;
        }

        @Override
        public List<Annotation> getSetterAnnotations() {
            return setterAnnotations;
        }

        @Override
        public List<Annotation> getFieldAnnotations() {
            return fieldAnnotations;
        }

        @Override
        public List<Annotation> getAnnotations() {
            return allAnnotations;
        }

        @Override
        public FsBean getOwner() {
            return owner;
        }

        @Override
        public boolean isReadable() {
            return getterInvoker != null;
        }

        @Override
        public boolean isWriteable() {
            return setterInvoker != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DefaultFsBeanPropertyImpl)) {
                return false;
            }
            DefaultFsBeanPropertyImpl other = (DefaultFsBeanPropertyImpl) o;
            return Objects.equals(owner.getType(), other.owner.getType())
                && Objects.equals(name, other.name)
                && Objects.equals(type, other.type);
        }

        @Override
        protected int computeHashCode() {
            return Objects.hash(owner.getType(), name, type);
        }

        @Override
        protected String computeToString() {
            return "BeanProperty(name=" +
                name +
                ", type=" +
                type +
                ", ownerType=" +
                owner.getType() +
                ")";
        }
    }
}
