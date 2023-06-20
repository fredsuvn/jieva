package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsCase;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.cache.FsCache;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.FsInvoker;
import xyz.srclab.common.reflect.FsReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Resolver for {@link FsBean}.
 *
 * @author sunq62
 */
public interface FsBeanResolver {

    /**
     * Returns default bean resolver,
     * which invoke mode is {@link Builder#USE_REFLECT} and property naming is {@link Builder#BEAN_NAMING}.
     * And this resolver will cache the bean by {@link FsCache#newCache()}.
     */
    static FsBeanResolver defaultResolver() {
        return FsUnsafe.ForBean.DEFAULT_RESOLVER;
    }

    /**
     * Returns a new bean resolver builder.
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Resolves given type to {@link FsBean}.
     *
     * @param type given type
     */
    FsBean resolve(Type type);

    class Builder {

        /**
         * Reflect mode.
         */
        public static final int USE_REFLECT = 1;
        /**
         * Un-reflect mode.
         */
        public static final int USE_UNREFLECT = 2;
        /**
         * Bean naming.
         */
        public static final int BEAN_NAMING = 1;
        /**
         * Record naming.
         */
        public static final int RECORD_NAMING = 2;

        private int invokeMode = USE_REFLECT;
        private int propertyNaming = BEAN_NAMING;

        /**
         * Sets invoke mode for getter and setter methods:
         * <ul>
         *     <li>
         *         {@link #USE_REFLECT}: Using reflect to invoke getter and setter methods.
         *     </li>
         *     <li>
         *         {@link #USE_UNREFLECT}: Using un-reflect ({@link java.lang.invoke.MethodHandles})
         *         to invoke getter and setter methods.
         *     </li>
         * </ul>
         *
         * @param invokeMode invoke mode
         */
        public Builder invokeMode(int invokeMode) {
            if (invokeMode != USE_REFLECT && invokeMode != USE_UNREFLECT) {
                throw new IllegalArgumentException("Unknown invoke mode: " + invokeMode);
            }
            this.invokeMode = invokeMode;
            return this;
        }

        /**
         * Sets property naming for getter and setter methods:
         * <ul>
         *     <li>
         *         {@link #BEAN_NAMING}: Starting with "get"/"set" for each getter and setter methods.
         *     </li>
         *     <li>
         *         {@link #RECORD_NAMING}: Directly using property name for getter methods,
         *         and starting with "set" for setter methods.
         *     </li>
         * </ul>
         *
         * @param propertyNaming property naming
         */
        public Builder propertyNaming(int propertyNaming) {
            if (propertyNaming != BEAN_NAMING && propertyNaming != RECORD_NAMING) {
                throw new IllegalArgumentException("Unknown property naming: " + propertyNaming);
            }
            this.propertyNaming = propertyNaming;
            return this;
        }

        public FsBeanResolver build() {
            return new FsBeanResolverImpl(invokeMode, propertyNaming);
        }

        private static final class FsBeanResolverImpl implements FsBeanResolver {

            private final int invokeMode;
            private final int propertyNaming;
            private final FsCache<FsBean> cache = FsCache.newCache();

            private FsBeanResolverImpl(int invokeMode, int propertyNaming) {
                this.invokeMode = invokeMode;
                this.propertyNaming = propertyNaming;
            }

            @Override
            public FsBean resolve(Type type) {
                return cache.get(type, this::resolve0);
            }

            public FsBean resolve0(Type type) {
                Class<?> rawType;
                if (type instanceof Class) {
                    rawType = (Class<?>) type;
                } else if (type instanceof ParameterizedType) {
                    rawType = ((Class<?>) ((ParameterizedType) type).getRawType());
                } else {
                    throw new IllegalArgumentException("The type to be resolved must be Class or ParameterizedType.");
                }
                Method[] methods = rawType.getMethods();
                Map<TypeVariable<?>, Type> typeParameterMapping = FsReflect.getTypeParameterMapping(type);
                Map<String, Method> getters = new LinkedHashMap<>();
                Map<String, Method> setters = new LinkedHashMap<>();
                for (Method method : methods) {
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
                FsBeanImpl bean = new FsBeanImpl();
                Set<Type> stack = new HashSet<>();
                getters.forEach((name, getter) -> {
                    Method setter = setters.get(name);
                    // Type of getter and setter must be same.
                    // But field's type will not be checked,
                    // because field is only a name holder for adding Annotations conveniently.
                    Type returnType = getter.getGenericReturnType();
                    returnType = FsCollect.nestedGet(typeParameterMapping, returnType, stack);
                    if (setter != null) {
                        Type setType = setter.getGenericParameterTypes()[0];
                        stack.clear();
                        setType = FsCollect.nestedGet(typeParameterMapping, setType, stack);
                        if (!Objects.equals(returnType, setType)) {
                            return;
                        }
                    }
                    Field field = findField(name, rawType);
                    bean.properties.put(name, new FsBeanPropertyImpl(bean, name, getter, setter, field, returnType));
                    setters.remove(name);
                });
                setters.forEach((name, setter) -> {
                    Field field = findField(name, rawType);
                    Type setType = setter.getGenericParameterTypes()[0];
                    stack.clear();
                    setType = FsCollect.nestedGet(typeParameterMapping, setType, stack);
                    bean.properties.put(name, new FsBeanPropertyImpl(bean, name, null, setter, field, setType));
                });
                bean.afterInit();
                return bean;
            }

            @Nullable
            private String isGetter(Method method) {
                switch (propertyNaming) {
                    case BEAN_NAMING:
                        if (method.getParameterCount() == 0
                            && method.getName().length() > 3
                            && method.getName().startsWith("get")
                        ) {
                            List<CharSequence> words = FsCase.LOWER_CAMEL.split(method.getName());
                            if (!FsCollect.isEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "get")) {
                                return FsCase.LOWER_CAMEL.join(words.subList(1, words.size()));
                            }
                        }
                        return null;
                    case RECORD_NAMING:
                        if (method.getParameterCount() == 0) {
                            return method.getName();
                        }
                        return null;
                    default:
                        throw new IllegalStateException("Unknown property naming: " + propertyNaming);
                }
            }

            @Nullable
            private String isSetter(Method method) {
                switch (propertyNaming) {
                    case BEAN_NAMING:
                    case RECORD_NAMING:
                        if (method.getParameterCount() == 1
                            && method.getName().length() > 3
                            && method.getName().startsWith("set")
                        ) {
                            List<CharSequence> words = FsCase.LOWER_CAMEL.split(method.getName());
                            if (!FsCollect.isEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "set")) {
                                return FsCase.LOWER_CAMEL.join(words.subList(1, words.size()));
                            }
                        }
                        return null;
                    default:
                        throw new IllegalStateException("Unknown property naming: " + propertyNaming);
                }
            }

            @Nullable
            private Field findField(String name, Class<?> type) {
                try {
                    return type.getField(name);
                } catch (NoSuchFieldException e) {
                    Class<?> cur = type;
                    while (cur != null) {
                        try {
                            return type.getDeclaredField(name);
                        } catch (NoSuchFieldException ex) {
                            cur = type.getSuperclass();
                        }
                    }
                }
                return null;
            }

            private final class FsBeanImpl implements FsBean {

                private Map<String, FsBeanProperty> properties = new LinkedHashMap<>();

                @Override
                public Map<String, FsBeanProperty> getProperties() {
                    return properties;
                }

                private void afterInit() {
                    properties = Collections.unmodifiableMap(properties);
                }
            }

            private final class FsBeanPropertyImpl implements FsBeanProperty {

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

                private FsBeanPropertyImpl(
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
                        case USE_REFLECT -> {
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
                        }
                        case USE_UNREFLECT -> {
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
                        }
                        default -> throw new IllegalStateException("Unknown invoke mode setting: " + invokeMode);
                    }
                    getterAnnotations = getter == null ? Collections.emptyList() :
                        Collections.unmodifiableList(Arrays.asList(getter.getAnnotations()));
                    setterAnnotations = setter == null ? Collections.emptyList() :
                        Collections.unmodifiableList(Arrays.asList(setter.getAnnotations()));
                    fieldAnnotations = field == null ? Collections.emptyList() :
                        Collections.unmodifiableList(Arrays.asList(field.getAnnotations()));
                    List<Annotation> annotations = new ArrayList<>(getterAnnotations.size() + setterAnnotations.size() + fieldAnnotations.size());
                    annotations.addAll(getterAnnotations);
                    annotations.addAll(setterAnnotations);
                    annotations.addAll(fieldAnnotations);
                    allAnnotations = Collections.unmodifiableList(annotations);
                }

                @Override
                public @Nullable Object get(Object bean) {
                    if (getterInvoker == null) {
                        throw new IllegalStateException("Property is not readable: " + name);
                    }
                    return getterInvoker.invoke(bean);
                }

                @Override
                public void set(Object bean, @Nullable Object value) {
                    if (setterInvoker == null) {
                        throw new IllegalStateException("Property is not writeable: " + name);
                    }
                    setterInvoker.invoke(bean, value);
                }

                @Override
                public Type getType() {
                    return type;
                }

                @Override
                public Class<?> getRawType() {
                    return FsReflect.getRawType(type);
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
            }
        }
    }
}
