package xyz.srclab.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.base.FsCase;
import xyz.srclab.common.base.FsFinal;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.cache.FsCache;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.convert.UnsupportedConvertException;
import xyz.srclab.common.reflect.FsInvoker;
import xyz.srclab.common.reflect.FsType;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Bean info of object, usually resolved by {@link FsBean.Resolver}.
 *
 * @author fredsuvn
 * @see Resolver
 */
@ThreadSafe
public interface FsBean {

    /**
     * Returns default bean resolver of which settings are:
     * <ul>
     *     <li>
     *         Invoke mode: {@link Resolver.Builder#REFLECT_MODE};
     *     </li>
     *     <li>
     *         Property name mapping: {@link Resolver.Builder#BEAN_NAMING};
     *     </li>
     *     <li>
     *         Property/Method naming case: {@link FsCase#LOWER_CAMEL};
     *     </li>
     *     <li>
     *         Use cache: true ({@link FsCache#newCache()});
     *     </li>
     * </ul>
     */
    static Resolver defaultResolver() {
        return FsUnsafe.ForBean.DEFAULT_RESOLVER;
    }

    /**
     * Returns a new bean resolver builder.
     */
    static Resolver.Builder resolverBuilder() {
        return new Resolver.Builder();
    }

    /**
     * Return default bean copier.
     */
    static Copier defaultCopier() {
        return null;
    }

    /**
     * Resolves given type to bean structure by {@link Resolver#defaultResolver()}.
     *
     * @param type given type
     */
    static FsBean resolve(Type type) {
        return defaultResolver().resolve(type);
    }

    /**
     * Wraps given map with given map type,
     * If the given map type is null, the map type will be seen as Map&lt;Object, Object>.
     * <p>
     * Name of property is map key converted by {@link String#valueOf(Object)},
     * if more than one keys are transformed into the same name, the latter key-value pair will overwrite the former.
     * <p>
     * Result of {@link FsBean#getProperties()} is immutable, but content may be different for each time calling.
     * Because of the changes in given map, contents of return property map are also changed accordingly.
     *
     * @param map     given map
     * @param mapType given map type
     */
    static FsBean wrapMap(Map<?, ?> map, @Nullable Type mapType) {
        return defaultResolver().wrapMap(map, mapType);
    }

    /**
     * Returns type of this bean.
     */
    Type getType();

    /**
     * Returns raw type of this bean.
     */
    default Class<?> getRawType() {
        return FsType.getRawType(getType());
    }

    /**
     * Returns all properties in this bean.
     */
    Map<String, FsBeanProperty> getProperties();

    /**
     * Returns property with given name in this bean.
     *
     * @param name given name
     */
    @Nullable
    default FsBeanProperty getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * Bean copier, used to copy bean properties from source object to dest object.
     *
     * @author fredsuvn
     */
    interface Copier {

        /**
         * Conversion fail policy: ignore failed property.
         */
        int IGNORE = 1;

        /**
         * Conversion fail policy: throw {@link xyz.srclab.common.convert.UnsupportedConvertException}.
         */
        int THROW = 2;

        /**
         * Copies properties from source object to dest object.
         *
         * @param source source object
         * @param dest   dest object
         */
        default <T> T copyProperties(Object source, T dest) {
            return copyProperties(source, source.getClass(), dest, dest.getClass());
        }

        /**
         * Copies properties from source object to dest object.
         *
         * @param source     source object
         * @param sourceType type of source object
         * @param dest       dest object
         * @param destType   type of dest object
         */
        <T> T copyProperties(Object source, Type sourceType, T dest, Type destType);

        /**
         * Builder for {@link Copier}.
         */
        class Builder {

            private Resolver beanResolver;
            private @Nullable Function<CharSequence, String> nameConverter;
            private @Nullable FsConverter valueConverter;
            private @Nullable BiPredicate<FsBeanProperty, FsBeanProperty> propertyFilter;
            private @Nullable BiPredicate<@Nullable Object, FsBeanProperty> destValueFilter;
            private @Nullable Function<Type, Object> objectGenerator;

            /**
             * Sets bean resolver.
             */
            Builder beanResolver(Resolver beanResolver) {
                this.beanResolver = beanResolver;
                return this;
            }

            /**
             * Sets property name converter, to convert source property name to dest property name.
             * If dest property name is null or not found, the property will be ignored.
             * <p>
             * Maybe null if this copier doesn't need name conversion.
             */
            Builder nameConverter(@Nullable Function<CharSequence, String> nameConverter) {
                this.nameConverter = nameConverter;
                return this;
            }

            /**
             * Sets object converter to convert property value in types.
             * All properties which will be copied will be converted by this converter,
             * even types between source and dest properties are same.
             * <p>
             * Maybe null if this copier doesn't need value conversion, in this case,
             * only properties have same type between source and dest bean can be copied.
             */
            Builder valueConverter(@Nullable FsConverter valueConverter) {
                this.valueConverter = valueConverter;
                return this;
            }

            /**
             * Sets property filter.
             * The first property is source property, second is dest property.
             * <p>
             * If property filter is not null,
             * only the properties that pass through this filter (return true) will be copied.
             */
            Builder propertyFilter(@Nullable BiPredicate<FsBeanProperty, FsBeanProperty> propertyFilter) {
                this.propertyFilter = propertyFilter;
                return this;
            }

            /**
             * Sets dest value filter.
             * The first object is dest value (after converting), second is dest property.
             * <p>
             * If dest value filter is not null,
             * only the values that pass through this filter (return true, including null) will be set into corresponding
             * dest property.
             * <p>
             * If dest value filter is null,
             * only the non-null values will be set into corresponding dest property.
             */
            Builder destValueFilter(@Nullable BiPredicate<@Nullable Object, FsBeanProperty> destValueFilter) {
                this.destValueFilter = destValueFilter;
                return this;
            }

            /**
             * Sets object generator to generate new instance for dest bean type.
             * <p>
             * If this generator is null, the copier will use empty constructor to generate new instance.
             */
            Builder objectGenerator(@Nullable Function<Type, Object> objectGenerator) {
                this.objectGenerator = objectGenerator;
                return this;
            }

            /**
             * Builds copier.
             */
            Copier build() {
                return new CopierImpl(
                    beanResolver, nameConverter, valueConverter, propertyFilter, destValueFilter, 1);
            }

            private final class CopierImpl implements Copier {

                private final Resolver beanResolver;
                private final @Nullable Function<CharSequence, String> nameConverter;
                private final @Nullable FsConverter valueConverter;
                private final @Nullable BiPredicate<FsBeanProperty, FsBeanProperty> propertyFilter;
                private final @Nullable BiPredicate<@Nullable Object, FsBeanProperty> destValueFilter;
                private final int conversionFailPolicy;

                private CopierImpl(
                    Resolver beanResolver,
                    @Nullable Function<CharSequence, String> nameConverter,
                    @Nullable FsConverter valueConverter,
                    @Nullable BiPredicate<FsBeanProperty, FsBeanProperty> propertyFilter,
                    @Nullable BiPredicate<@Nullable Object, FsBeanProperty> destValueFilter,
                    int conversionFailPolicy
                ) {
                    this.beanResolver = beanResolver;
                    this.nameConverter = nameConverter;
                    this.valueConverter = valueConverter;
                    this.propertyFilter = propertyFilter;
                    this.destValueFilter = destValueFilter;
                    this.conversionFailPolicy = conversionFailPolicy;
                }

                @Override
                public <T> T copyProperties(Object source, Type sourceType, T dest, Type destType) {
                    FsBean sourceBean = (source instanceof Map) ?
                        beanResolver.wrapMap((Map<?, ?>) source, sourceType) : beanResolver.resolve(sourceType);
                    FsBean destBean = (dest instanceof Map) ?
                        beanResolver.wrapMap((Map<?, ?>) dest, destType) : beanResolver.resolve(destType);
                    sourceBean.getProperties().forEach((name, srcProperty) -> {
                        String destPropertyName = nameConverter == null ? name : nameConverter.apply(name);
                        if (destPropertyName == null) {
                            return;
                        }
                        FsBeanProperty destProperty = destBean.getProperty(destPropertyName);
                        if (destProperty == null) {
                            return;
                        }
                        if (propertyFilter != null && !propertyFilter.test(srcProperty, destProperty)) {
                            return;
                        }
                        Object destValue;
                        if (valueConverter != null) {
                            destValue = valueConverter.convert(
                                srcProperty.get(source), srcProperty.getType(), destProperty.getType(), valueConverter.getOptions());
                            if (destValue == FsConverter.UNSUPPORTED) {
                                // throw new UnsupportedConvertException()
                            }
                        } else {
                            if (Objects.equals(srcProperty.getType(), destProperty.getType())) {
                                destValue = srcProperty.get(source);
                            } else {
                                return;
                            }
                        }
                        if (destValueFilter != null && !destValueFilter.test(destValue, destProperty)) {
                            return;
                        }
                        destProperty.set(dest, destValue);
                    });
                    return null;
                }
            }
        }
    }

    /**
     * Resolver for {@link FsBean}.
     */
    interface Resolver {

        /**
         * Resolves given type to {@link FsBean}.
         *
         * @param type given type
         */
        FsBean resolve(Type type);

        /**
         * Wraps given map with given map type,
         * If the given map type is null, the map type will be seen as Map&lt;Object, Object>.
         * <p>
         * Name of property is map key converted by {@link String#valueOf(Object)},
         * if more than one keys are transformed into the same name, the latter key-value pair will overwrite the former.
         * <p>
         * Result of {@link FsBean#getProperties()} is immutable, but content may be different for each time calling.
         * Because of the changes in given map, contents of return property map are also changed accordingly.
         *
         * @param map     given map
         * @param mapType given map type
         */
        FsBean wrapMap(Map<?, ?> map, @Nullable Type mapType);

        /**
         * Builder for {@link Resolver}.
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

            public Resolver build() {
                return new ResolverImpl(
                    invokeMode, propertyNameMapping, propertyNamingCase, methodNamingCase, useCache);
            }

            private static final class ResolverImpl implements Resolver {

                private final int invokeMode;
                private final int propertyNaming;
                private final FsCase propertyNamingCase;
                private final FsCase methodNamingCase;
                private final FsCache<FsBean> cache;

                private ResolverImpl(
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
                                if (FsCollect.isNotEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "get")) {
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
                                if (FsCollect.isNotEmpty(words) && words.size() > 1 && Objects.equals(words.get(0).toString(), "set")) {
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

                @Override
                public FsBean wrapMap(Map<?, ?> map, @Nullable Type mapType) {
                    return new FsMapBeanImpl((Map<Object, Object>) map, mapType);
                }

                private static final class FsBeanImpl extends FsFinal implements FsBean {

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
                        return Objects.equals(type, ((FsBeanImpl) o).type)
                            && Objects.equals(properties, ((FsBeanImpl) o).properties);
                    }

                    @Override
                    protected int computeHashCode() {
                        return Objects.hash(properties);
                    }

                    @Override
                    protected String computeToString() {
                        return "bean(" + properties.entrySet().stream()
                            .map(it -> it.getKey() + ": " + it.getValue()).collect(Collectors.joining(", ")) +
                            ")";
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
                        return "BeanProperty(name=" +
                            name +
                            ", type=" +
                            type +
                            ", ownerType=" +
                            owner.getType() +
                            ")";
                    }
                }

                private static final class FsMapBeanImpl implements FsBean {

                    private static final Type DEFAULT_MAP_TYPE = new TypeRef<Map<Object, Object>>() {
                    }.getType();

                    private final Map<Object, Object> map;
                    private final Type mapType;
                    private final Type valueType;
                    private final Map<Object, Node> propertyNodes = new LinkedHashMap<>();
                    private int propertyVersion = 0;

                    private Map<String, FsBeanProperty> properties;

                    private FsMapBeanImpl(Map<Object, Object> map, @Nullable Type mapType) {
                        this.map = map;
                        this.mapType = mapType == null ? DEFAULT_MAP_TYPE : mapType;
                        if (this.mapType == DEFAULT_MAP_TYPE) {
                            this.valueType = Object.class;
                        } else {
                            ParameterizedType parameterizedType = FsType.getGenericSuperType(mapType, Map.class);
                            if (parameterizedType == null) {
                                throw new IllegalArgumentException("Not a map type: " + mapType + ".");
                            }
                            Type[] types = parameterizedType.getActualTypeArguments();
                            this.valueType = types[1];
                        }
                    }

                    @Override
                    public Type getType() {
                        return mapType;
                    }

                    @Override
                    public synchronized Map<String, FsBeanProperty> getProperties() {
                        // first init
                        if (properties == null) {
                            map.forEach((k, v) -> {
                                FsBeanProperty property = new FsMapBeanPropertyImpl(k);
                                propertyNodes.put(k, new Node(propertyVersion, property));
                            });
                            properties = Collections.unmodifiableMap(
                                FsCollect.mapMap(propertyNodes, new LinkedHashMap<>(), String::valueOf, Node::getProperty)
                            );
                            return properties;
                        }

                        propertyVersion++;
                        boolean hasNewNode = false;
                        Set<Object> keySet = map.keySet();
                        for (Object key : keySet) {
                            Node node = propertyNodes.get(key);
                            if (node == null) {
                                FsBeanProperty property = new FsMapBeanPropertyImpl(key);
                                propertyNodes.put(key, new Node(propertyVersion, property));
                                hasNewNode = true;
                            } else {
                                node.version = propertyVersion;
                            }
                        }
                        if (!hasNewNode && properties.size() == keySet.size()) {
                            return properties;
                        } else {
                            //remove expired nodes
                            Set<Object> nodeKeys = new HashSet<>(propertyNodes.keySet());
                            for (Object nodeKey : nodeKeys) {
                                Node node = propertyNodes.get(nodeKey);
                                if (node == null) {
                                    continue;
                                }
                                if (node.version != propertyVersion) {
                                    propertyNodes.remove(nodeKey);
                                }
                            }
                        }
                        properties = Collections.unmodifiableMap(
                            FsCollect.mapMap(propertyNodes, new LinkedHashMap<>(), String::valueOf, Node::getProperty)
                        );
                        return properties;
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) {
                            return true;
                        }
                        if (!(o instanceof FsMapBeanImpl)) {
                            return false;
                        }
                        return Objects.equals(mapType, ((FsMapBeanImpl) o).mapType)
                            && Objects.equals(map, ((FsMapBeanImpl) o).map);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(mapType, map.hashCode());
                    }

                    @Override
                    public String toString() {
                        return "bean(" + getProperties().entrySet().stream()
                            .map(it -> it.getKey() + ": " + it.getValue()).collect(Collectors.joining(", ")) +
                            ")";
                    }

                    @Data
                    @AllArgsConstructor
                    private final class Node {
                        private int version;
                        private FsBeanProperty property;
                    }

                    private final class FsMapBeanPropertyImpl implements FsBeanProperty {

                        private final Object key;

                        private FsMapBeanPropertyImpl(Object key) {
                            this.key = key;
                        }

                        @Override
                        public @Nullable Object get(Object bean) {
                            return map.get(key);
                        }

                        @Override
                        public void set(Object bean, @Nullable Object value) {
                            map.put(key, value);
                        }

                        @Override
                        public Type getType() {
                            return valueType;
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
                            return FsMapBeanImpl.this;
                        }

                        @Override
                        public boolean isReadable() {
                            return true;
                        }

                        @Override
                        public boolean isWriteable() {
                            return true;
                        }

                        @Override
                        public boolean equals(Object o) {
                            if (this == o) {
                                return true;
                            }
                            if (!(o instanceof FsMapBeanPropertyImpl)) {
                                return false;
                            }
                            return Objects.equals(key, ((FsMapBeanPropertyImpl) o).key)
                                && Objects.equals(FsMapBeanImpl.this, ((FsMapBeanPropertyImpl) o).getOwner());
                        }

                        @Override
                        public int hashCode() {
                            return Objects.hash(key);
                        }

                        @Override
                        public String toString() {
                            return "BeanProperty(name=" +
                                key +
                                ", type=" +
                                valueType +
                                ", ownerType=" +
                                getOwner().getType() +
                                ")";
                        }
                    }
                }
            }
        }
    }
}
