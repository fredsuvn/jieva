package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsCase;
import xyz.srclab.common.base.FsFinal;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.cache.FsCache;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.FsInvoker;
import xyz.srclab.common.reflect.FsReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Resolver for {@link FsBean}.
 *
 * @author fredsuvn
 */
public interface FsBeanResolver {

    /**
     * Returns default bean resolver,
     * which invoke mode is {@link Builder#REFLECT_MODE}, property name mapping is {@link Builder#BEAN_NAMING}
     * and property/method naming case is {@link FsCase#LOWER_CAMEL}.
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

    /**
     * Builder for {@link FsBeanResolver}.
     */
    class Builder {

        /**
         * Reflect mode.
         */
        public static final int REFLECT_MODE = 1;
        /**
         * Un-reflect mode.
         */
        public static final int UNREFLECT_MODE = 2;
        /**
         * Bean naming.
         */
        public static final int BEAN_NAMING = 1;
        /**
         * Record naming.
         */
        public static final int RECORD_NAMING = 2;

        private int invokeMode = REFLECT_MODE;
        private int propertyNameMapping = BEAN_NAMING;
        private FsCase propertyNamingCase = FsCase.LOWER_CAMEL;
        private FsCase methodNamingCase = FsCase.LOWER_CAMEL;

        private boolean useCache = true;

        /**
         * Sets invoke mode for getter and setter methods:
         * <ul>
         *     <li>
         *         {@link #REFLECT_MODE}: Using reflect to invoke getter and setter methods.
         *     </li>
         *     <li>
         *         {@link #UNREFLECT_MODE}: Using un-reflect ({@link java.lang.invoke.MethodHandles})
         *         to invoke getter and setter methods.
         *     </li>
         * </ul>
         *  Default is {@link #REFLECT_MODE}.
         *
         * @param invokeMode invoke mode
         */
        public Builder invokeMode(int invokeMode) {
            if (invokeMode != REFLECT_MODE && invokeMode != UNREFLECT_MODE) {
                throw new IllegalArgumentException("Unknown invoke mode: " + invokeMode);
            }
            this.invokeMode = invokeMode;
            return this;
        }

        /**
         * Sets property name mapping for getter and setter methods:
         * <ul>
         *     <li>
         *         {@link #BEAN_NAMING}: Starting with "get"/"set" for each getter and setter methods.
         *     </li>
         *     <li>
         *         {@link #RECORD_NAMING}: Directly using property name for getter methods,
         *         and starting with "set" for setter methods.
         *     </li>
         * </ul>
         *  Default is {@link #BEAN_NAMING}.
         *
         * @param propertyNameMapping property naming
         */
        public Builder propertyNameMapping(int propertyNameMapping) {
            if (propertyNameMapping != BEAN_NAMING && propertyNameMapping != RECORD_NAMING) {
                throw new IllegalArgumentException("Unknown property naming: " + propertyNameMapping);
            }
            this.propertyNameMapping = propertyNameMapping;
            return this;
        }

        /**
         * Sets property naming case. Default is {@link FsCase#LOWER_CAMEL}.
         *
         * @param propertyNamingCase property naming case
         */
        public Builder propertyNamingCase(FsCase propertyNamingCase) {
            this.propertyNamingCase = propertyNamingCase;
            return this;
        }

        /**
         * Sets method naming case. Default is {@link FsCase#LOWER_CAMEL}.
         *
         * @param methodNamingCase method naming case
         */
        public Builder methodNamingCase(FsCase methodNamingCase) {
            this.methodNamingCase = methodNamingCase;
            return this;
        }

        /**
         * Sets whether the resolver cache the resolved bean. Default is true.
         *
         * @param useCache whether the resolver cache the resolved bean
         */
        public Builder useCache(boolean useCache) {
            this.useCache = useCache;
            return this;
        }

        public FsBeanResolver build() {
            return new FsBeanResolverImpl(
                invokeMode, propertyNameMapping, propertyNamingCase, methodNamingCase, useCache);
        }

        private static final class FsBeanResolverImpl implements FsBeanResolver {

            private final int invokeMode;
            private final int propertyNaming;
            private final FsCase propertyNamingCase;
            private final FsCase methodNamingCase;
            private final FsCache<FsBean> cache;

            private FsBeanResolverImpl(
                int invokeMode, int propertyNaming, FsCase propertyNamingCase, FsCase methodNamingCase, boolean useCache) {
                this.invokeMode = invokeMode;
                this.propertyNaming = propertyNaming;
                this.propertyNamingCase = propertyNamingCase;
                this.methodNamingCase = methodNamingCase;
                if (useCache) {
                    cache = FsCache.newCache();
                } else {
                    cache = null;
                }
            }

            @Override
            public FsBean resolve(Type type) {
                if (cache == null) {
                    return resolve0(type);
                }
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
                FsBeanImpl bean = new FsBeanImpl(type);
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
                    bean.properties.put(name, new FsBeanPropertyImpl(bean, name, getter, setter, field, returnType));
                    setters.remove(name);
                });
                setters.forEach((name, setter) -> {
                    Field field = findField(name, rawType);
                    Type setType = setter.getGenericParameterTypes()[0];
                    setType = getActualType(setType, typeParameterMapping, stack);
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
                            List<CharSequence> words = methodNamingCase.split(method.getName());
                            if (!FsCollect.isEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "get")) {
                                return propertyNamingCase.join(words.subList(1, words.size()));
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
                            List<CharSequence> words = methodNamingCase.split(method.getName());
                            if (!FsCollect.isEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "set")) {
                                return propertyNamingCase.join(words.subList(1, words.size()));
                            }
                        }
                        return null;
                    default:
                        throw new IllegalStateException("Unknown property naming: " + propertyNaming);
                }
            }

            private Type getActualType(Type type, Map<TypeVariable<?>, Type> typeParameterMapping, Set<Type> stack) {
                if (type instanceof Class) {
                    return type;
                }
                stack.clear();
                Type result = FsCollect.nestedGet(typeParameterMapping, type, stack);
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

            private final class FsBeanImpl extends FsFinal implements FsBean {

                private final Type type;
                private Map<String, FsBeanProperty> properties = new LinkedHashMap<>();

                private FsBeanImpl(Type type) {
                    this.type = type;
                }

                @Override
                public Type getType() {
                    return type;
                }

                @Override
                public Map<String, FsBeanProperty> getProperties() {
                    return properties;
                }

                private void afterInit() {
                    properties = Collections.unmodifiableMap(properties);
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (!(o instanceof FsBeanImpl)) {
                        return false;
                    }
                    return Objects.equals(properties, ((FsBeanImpl) o).properties);
                }

                @Override
                protected int computeHashCode() {
                    return Objects.hash(properties);
                }

                @Override
                protected String computeToString() {
                    StringBuilder sb = new StringBuilder("bean(");
                    sb.append(properties.entrySet().stream()
                        .map(it -> it.getKey() + ": " + it.getValue()).collect(Collectors.joining(", ")));
                    sb.append(")");
                    return sb.toString();
                }
            }

            private final class FsBeanPropertyImpl extends FsFinal implements FsBeanProperty {

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
                            throw new IllegalStateException("Unknown invoke mode setting: " + invokeMode);
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
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (!(o instanceof FsBeanPropertyImpl)) {
                        return false;
                    }
                    FsBeanPropertyImpl other = (FsBeanPropertyImpl) o;
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
                    String sb = "BeanProperty(name=" +
                        name +
                        ", type=" +
                        type +
                        ", ownerType=" +
                        owner.getType() +
                        ")";
                    return sb;
                }
            }
        }
    }
}
