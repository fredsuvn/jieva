package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.convert.GekConvertException;
import xyz.fsgek.common.convert.GekConverter;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Bean properties copier for {@link GekBean}, to copy properties from source object to dest object.
 * The copier supports both common object and {@link Map}.
 *
 * @author fredsuvn
 */
public interface GekBeanCopier {

    /**
     * Returns default bean copier.
     *
     * @return default bean copier
     */
    static GekBeanCopier defaultCopier() {
        return Builder.DEFAULT;
    }

    /**
     * Returns new builder of {@link GekBeanCopier}.
     *
     * @return new builder of {@link GekBeanCopier}
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Copies properties from source object to dest object.
     *
     * @param source source object
     * @param dest   dest object
     * @param <T>    type of dest object
     * @return dest object
     */
    default <T> T copyProperties(Object source, T dest) {
        return copyProperties(source, source.getClass(), dest, dest.getClass());
    }

    /**
     * Copies properties from source object to dest object with specified types.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     * @param <T>        type of dest object
     * @return dest object
     */
    <T> T copyProperties(Object source, Type sourceType, T dest, Type destType);

    /**
     * Returns a bean copier of which options come from this copier,
     * but the bean resolver will be set to given resolver.
     *
     * @param resolver given resolver
     * @return a new copier
     */
    default GekBeanCopier withBeanResolver(GekBeanResolver resolver) {
        return toBuilder().beanResolver(resolver).build();
    }

    /**
     * Returns a bean copier of which options come from this copier,
     * but the object converter will be set to given converter.
     *
     * @param converter given converter
     * @return a new copier
     */
    default GekBeanCopier withConverter(GekConverter converter) {
        return toBuilder().converter(converter).build();
    }

    /**
     * Returns a new builder of current copier.
     *
     * @return a new builder of current copier
     */
    Builder toBuilder();

    /**
     * Builder for {@link GekBeanCopier}.
     */
    class Builder {

        private static final GekBeanCopier DEFAULT = newBuilder().build();

        private @Nullable GekBeanResolver beanResolver;
        private @Nullable GekConverter converter;
        private boolean throwIfConvertFailed = false;
        private @Nullable Function<Object, Object> propertyNameMapper;
        private @Nullable BiPredicate<Object, @Nullable Object> sourcePropertyFilter;
        private @Nullable BiPredicate<Object, @Nullable Object> destPropertyFilter;
        private boolean putIfNotContained = true;

        /**
         * Sets bean resolver for copy operation.
         * Default is null, in this case the operation will use {@link GekBeanResolver#defaultResolver()}.
         *
         * @param beanResolver bean resolver
         * @return this builder
         */
        public Builder beanResolver(GekBeanResolver beanResolver) {
            this.beanResolver = beanResolver;
            return this;
        }

        /**
         * Sets object converter for copy operation.
         * Default is null, in this case the operation will use {@link GekConverter#defaultConverter()}.
         *
         * @param converter converter
         * @return this builder
         */
        public Builder converter(GekConverter converter) {
            this.converter = converter;
            return this;
        }

        /**
         * Sets whether throws {@link GekConvertException} if conversion operation was failed.
         * Default is false, means ignore failed properties.
         *
         * @param throwIfConvertFailed whether throws {@link GekConvertException} if conversion operation was failed
         * @return this builder
         */
        public Builder throwIfConvertFailed(boolean throwIfConvertFailed) {
            this.throwIfConvertFailed = throwIfConvertFailed;
            return this;
        }

        /**
         * Sets property name mapper, to map property names from source object to dest object.
         * The property will be ignored if new name is null or not found in dest bean.
         * <p>
         * For common bean object, type of property names is always {@link String};
         * for map object, type of keys is any type.
         * <p>
         * Note:
         * <ul>
         *     <li>
         *         Type of property name/key must same before and after mapping;
         *     </li>
         *     <li>
         *         The new property name/key after mapping still needs to be converted by the converter
         *         into the final property name/key to be used.
         *     </li>
         * </ul>
         *
         * @param propertyNameMapper property name mapper
         * @param <T>                type of name
         * @return this builder
         */
        public <T> Builder propertyNameMapper(Function<T, T> propertyNameMapper) {
            this.propertyNameMapper = Gek.as(propertyNameMapper);
            return this;
        }

        /**
         * Sets source property filter,
         * the first param is name of source property, second is value of source property value.
         * <p>
         * Only the property that pass through this filter (return true) will be copied from.
         *
         * @param sourcePropertyFilter source property filter
         * @return this builder
         */
        public Builder sourcePropertyFilter(BiPredicate<Object, @Nullable Object> sourcePropertyFilter) {
            this.sourcePropertyFilter = sourcePropertyFilter;
            return this;
        }

        /**
         * Sets dest property filter,
         * the first param is name of dest property, second is converted value of source property (maybe null)
         * that is prepared to copy.
         * <p>
         * Only the property that pass through this filter (return true) will be copied from.
         *
         * @param destPropertyFilter dest property filter
         * @return this builder
         */
        public Builder destPropertyFilter(BiPredicate<Object, @Nullable Object> destPropertyFilter) {
            this.destPropertyFilter = destPropertyFilter;
            return this;
        }

        /**
         * Sets whether put the property into dest map if dest map doesn't contain corresponding property.
         * Default is true.
         *
         * @param putIfNotContained whether put the property into dest map
         * @return this builder
         */
        public Builder putIfNotContained(boolean putIfNotContained) {
            this.putIfNotContained = putIfNotContained;
            return this;
        }

        /**
         * Builds {@link GekBeanCopier}.
         *
         * @return built {@link GekBeanCopier}
         */
        public GekBeanCopier build() {
            return new BeanCopierImpl(this);
        }

        private static final class BeanCopierImpl implements GekBeanCopier {

            private final @Nullable GekBeanResolver beanResolver;
            private final @Nullable GekConverter converter;
            private final boolean throwIfConvertFailed;
            private final @Nullable Function<Object, Object> propertyNameMapper;
            private final @Nullable BiPredicate<Object, @Nullable Object> sourcePropertyFilter;
            private final @Nullable BiPredicate<Object, @Nullable Object> destPropertyFilter;
            private final boolean putIfNotContained;

            private BeanCopierImpl(Builder builder) {
                this.beanResolver = builder.beanResolver;
                this.converter = builder.converter;
                this.throwIfConvertFailed = builder.throwIfConvertFailed;
                this.propertyNameMapper = builder.propertyNameMapper;
                this.sourcePropertyFilter = builder.sourcePropertyFilter;
                this.destPropertyFilter = builder.destPropertyFilter;
                this.putIfNotContained = builder.putIfNotContained;
            }

            @Override
            public <T> T copyProperties(Object source, Type sourceType, T dest, Type destType) {
                if (source instanceof Map) {
                    ParameterizedType sourceMapType = GekReflect.getGenericSuperType(sourceType, Map.class);
                    if (sourceMapType == null) {
                        throw new IllegalArgumentException("Not a map type: " + sourceType + ".");
                    }
                    Type[] sourceActualTypes = sourceMapType.getActualTypeArguments();
                    Type sourceKeyType = sourceActualTypes[0];
                    Type sourceValueType = sourceActualTypes[1];
                    if (dest instanceof Map) {
                        ParameterizedType destMapType = GekReflect.getGenericSuperType(destType, Map.class);
                        if (destMapType == null) {
                            throw new IllegalArgumentException("Not a map type: " + destType + ".");
                        }
                        Type[] destActualTypes = destMapType.getActualTypeArguments();
                        Type destKeyType = destActualTypes[0];
                        Type destValueType = destActualTypes[1];
                        copyProperties0(source, sourceType, sourceKeyType, sourceValueType, dest, destType, destKeyType, destValueType);
                    } else {
                        copyProperties0(source, sourceType, sourceKeyType, sourceValueType, dest, destType, null, null);
                    }
                } else {
                    if (dest instanceof Map) {
                        ParameterizedType destMapType = GekReflect.getGenericSuperType(destType, Map.class);
                        if (destMapType == null) {
                            throw new IllegalArgumentException("Not a map type: " + destType + ".");
                        }
                        Type[] destActualTypes = destMapType.getActualTypeArguments();
                        Type destKeyType = destActualTypes[0];
                        Type destValueType = destActualTypes[1];
                        copyProperties0(source, sourceType, null, null, dest, destType, destKeyType, destValueType);
                    } else {
                        copyProperties0(source, sourceType, null, null, dest, destType, null, null);
                    }
                }
                return dest;
            }

            private void copyProperties0(
                Object source, Type sourceType, Type sourceKeyType, Type sourceValueType,
                Object dest, Type destType, Type destKeyType, Type destValueType
            ) {
                GekConverter converter =
                    this.converter == null ? GekConverter.defaultConverter() : this.converter;
                Function<Object, Object> nameMapper =
                    this.propertyNameMapper == null ? n -> n : this.propertyNameMapper;
                BiPredicate<Object, @Nullable Object> sourceFilter =
                    this.sourcePropertyFilter == null ? (k, v) -> true : this.sourcePropertyFilter;
                BiPredicate<Object, @Nullable Object> destFilter =
                    this.destPropertyFilter == null ? (k, v) -> true : this.destPropertyFilter;
                if (source instanceof Map) {
                    if (dest instanceof Map) {
                        Map<Object, Object> sourceMap = Gek.as(source);
                        Map<Object, Object> destMap = Gek.as(dest);
                        sourceMap.forEach((key, value) -> {
                            if (!sourceFilter.test(key, value)) {
                                return;
                            }
                            Object destKey = nameMapper.apply(key);
                            if (destKey == null) {
                                return;
                            }
                            Object newDestKey = tryConvert(destKey, sourceKeyType, destKeyType, converter);
                            if (newDestKey == null) {
                                return;
                            } else {
                                newDestKey = GekConverter.getResult(newDestKey);
                            }
                            if (!putIfNotContained && !destMap.containsKey(newDestKey)) {
                                return;
                            }
                            Object newDestValue = tryConvert(value, sourceValueType, destValueType, converter);
                            if (newDestValue == null) {
                                return;
                            } else {
                                newDestValue = GekConverter.getResult(newDestValue);
                            }
                            if (!destFilter.test(newDestKey, newDestValue)) {
                                return;
                            }
                            destMap.put(newDestKey, newDestValue);
                        });
                    } else {
                        GekBeanResolver resolver =
                            this.beanResolver == null ? GekBeanResolver.defaultResolver() : this.beanResolver;
                        Map<Object, Object> sourceMap = Gek.as(source);
                        GekBean destBean = resolver.resolve(destType);
                        sourceMap.forEach((key, value) -> {
                            if (!sourceFilter.test(key, value)) {
                                return;
                            }
                            Object destNameObj = nameMapper.apply(key);
                            if (destNameObj == null) {
                                return;
                            }
                            Object newDestNameObj = tryConvert(destNameObj, sourceKeyType, String.class, converter);
                            if (newDestNameObj == null) {
                                return;
                            } else {
                                newDestNameObj = GekConverter.getResult(newDestNameObj);
                            }
                            String newDestName = (String) newDestNameObj;
                            GekProperty destProperty = destBean.getProperty(newDestName);
                            if (destProperty == null || !destProperty.isWriteable()) {
                                return;
                            }
                            Object newDestValue = tryConvert(value, sourceValueType, destProperty.getType(), converter);
                            if (newDestValue == null) {
                                return;
                            } else {
                                newDestValue = GekConverter.getResult(newDestValue);
                            }
                            if (!destFilter.test(newDestName, newDestValue)) {
                                return;
                            }
                            destProperty.set(dest, newDestValue);
                        });
                    }
                } else {
                    GekBeanResolver resolver =
                        this.beanResolver == null ? GekBeanResolver.defaultResolver() : this.beanResolver;
                    GekBean sourceBean = resolver.resolve(sourceType);
                    if (dest instanceof Map) {
                        Map<Object, Object> destMap = Gek.as(dest);
                        sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                            if (!sourceProperty.isReadable()) {
                                return;
                            }
                            Object sourceValue = sourceProperty.get(source);
                            if (!sourceFilter.test(sourceName, sourceValue)) {
                                return;
                            }
                            Object destKey = nameMapper.apply(sourceName);
                            if (destKey == null) {
                                return;
                            }
                            Object newDestKey = tryConvert(destKey, String.class, destKeyType, converter);
                            if (newDestKey == null) {
                                return;
                            } else {
                                newDestKey = GekConverter.getResult(newDestKey);
                            }
                            if (!putIfNotContained && !destMap.containsKey(destKey)) {
                                return;
                            }
                            Object newDestValue = tryConvert(sourceValue, sourceProperty.getType(), destValueType, converter);
                            if (newDestValue == null) {
                                return;
                            } else {
                                newDestValue = GekConverter.getResult(newDestValue);
                            }
                            if (!destFilter.test(destKey, newDestValue)) {
                                return;
                            }
                            destMap.put(newDestKey, newDestValue);
                        });
                    } else {
                        GekBean destBean = resolver.resolve(destType);
                        sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                            if (!sourceProperty.isReadable()) {
                                return;
                            }
                            Object sourceValue = sourceProperty.get(source);
                            if (!sourceFilter.test(sourceName, sourceValue)) {
                                return;
                            }
                            Object destNameObj = nameMapper.apply(sourceName);
                            if (destNameObj == null) {
                                return;
                            }
                            Object newDestNameObj = tryConvert(destNameObj, String.class, String.class, converter);
                            if (newDestNameObj == null) {
                                return;
                            } else {
                                newDestNameObj = GekConverter.getResult(newDestNameObj);
                            }
                            String newDestName = (String) newDestNameObj;
                            GekProperty destProperty = destBean.getProperty(newDestName);
                            if (destProperty == null || !destProperty.isWriteable()) {
                                return;
                            }
                            Object newDestValue = tryConvert(sourceValue, sourceProperty.getType(), destProperty.getType(), converter);
                            if (newDestValue == null) {
                                return;
                            } else {
                                newDestValue = GekConverter.getResult(newDestValue);
                            }
                            if (!destFilter.test(newDestName, newDestValue)) {
                                return;
                            }
                            destProperty.set(dest, newDestValue);
                        });
                    }
                }
            }

            @Nullable
            private Object tryConvert(Object value, Type fromType, Type destType, GekConverter converter) {
                Object newValue = converter.convertType(value, fromType, destType);
                if (newValue == null) {
                    if (throwIfConvertFailed) {
                        throw new GekConvertException(fromType, destType);
                    } else {
                        return null;
                    }
                }
                return newValue;
            }

            @Override
            public GekBeanCopier withBeanResolver(GekBeanResolver resolver) {
                if (Objects.equals(this.beanResolver, resolver)) {
                    return this;
                }
                return GekBeanCopier.super.withBeanResolver(resolver);
            }

            @Override
            public GekBeanCopier withConverter(GekConverter converter) {
                if (Objects.equals(this.converter, converter)) {
                    return this;
                }
                return GekBeanCopier.super.withConverter(converter);
            }

            @Override
            public Builder toBuilder() {
                return newBuilder()
                    .beanResolver(beanResolver)
                    .converter(converter)
                    .throwIfConvertFailed(throwIfConvertFailed)
                    .propertyNameMapper(propertyNameMapper)
                    .sourcePropertyFilter(sourcePropertyFilter)
                    .destPropertyFilter(destPropertyFilter)
                    .putIfNotContained(putIfNotContained);
            }
        }
    }
}
