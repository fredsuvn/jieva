package xyz.fsgik.common.bean;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.base.Fs;
import xyz.fsgik.common.convert.FsConvertException;
import xyz.fsgik.common.convert.FsConverter;
import xyz.fsgik.common.reflect.FsReflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Bean properties copier for {@link FsBean}, to copy properties from source object to dest object.
 * The copier supports both common object and {@link Map}.
 *
 * @author fredsuvn
 */
public interface FsBeanCopier {

    /**
     * Returns default bean copier.
     */
    static FsBeanCopier defaultCopier() {
        return Builder.DEFAULT;
    }

    /**
     * Returns new builder of {@link FsBeanCopier}.
     */
    static Builder newBuilder() {
        return new Builder();
    }

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
     * Copies properties from source object to dest object with specified types.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     */
    <T> T copyProperties(Object source, Type sourceType, T dest, Type destType);

    /**
     * Returns a bean copier of which options come from this copier,
     * but the bean resolver will be set to given resolver.
     *
     * @param resolver given resolver
     */
    default FsBeanCopier withBeanResolver(FsBeanResolver resolver) {
        return toBuilder().beanResolver(resolver).build();
    }

    /**
     * Returns a bean copier of which options come from this copier,
     * but the object converter will be set to given converter.
     *
     * @param converter given converter
     */
    default FsBeanCopier withConverter(FsConverter converter) {
        return toBuilder().converter(converter).build();
    }

    /**
     * Returns a new builder of current copier.
     */
    Builder toBuilder();

    /**
     * Builder for {@link FsBeanCopier}.
     */
    class Builder {

        private static final FsBeanCopier DEFAULT = newBuilder().build();

        private @Nullable FsBeanResolver beanResolver;
        private @Nullable FsConverter converter;
        private boolean throwIfConvertFailed = false;
        private @Nullable Function<Object, Object> propertyNameMapper;
        private @Nullable BiPredicate<Object, @Nullable Object> sourcePropertyFilter;
        private @Nullable BiPredicate<Object, @Nullable Object> destPropertyFilter;
        private boolean putIfNotContained = true;

        /**
         * Sets bean resolver for copy operation.
         * Default is null, in this case the operation will use {@link FsBeanResolver#defaultResolver()}.
         */
        public Builder beanResolver(FsBeanResolver beanResolver) {
            this.beanResolver = beanResolver;
            return this;
        }

        /**
         * Sets object converter for copy operation.
         * Default is null, in this case the operation will use {@link FsConverter#defaultConverter()}.
         */
        public Builder converter(FsConverter converter) {
            this.converter = converter;
            return this;
        }

        /**
         * Sets whether throws {@link FsConvertException} if conversion operation was failed.
         * Default is false, means ignore failed properties.
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
         */
        public <T> Builder propertyNameMapper(Function<T, T> propertyNameMapper) {
            this.propertyNameMapper = Fs.as(propertyNameMapper);
            return this;
        }

        /**
         * Sets source property filter,
         * the first param is name of source property, second is value of source property value.
         * <p>
         * Only the property that pass through this filter (return true) will be copied from.
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
         */
        public Builder destPropertyFilter(BiPredicate<Object, @Nullable Object> destPropertyFilter) {
            this.destPropertyFilter = destPropertyFilter;
            return this;
        }

        /**
         * Sets whether put the property into dest map if dest map doesn't contain corresponding property.
         * Default is true.
         */
        public Builder putIfNotContained(boolean putIfNotContained) {
            this.putIfNotContained = putIfNotContained;
            return this;
        }

        /**
         * Builds {@link FsBeanCopier}.
         */
        public FsBeanCopier build() {
            return new BeanCopierImpl(this);
        }

        private static final class BeanCopierImpl implements FsBeanCopier {

            private final @Nullable FsBeanResolver beanResolver;
            private final @Nullable FsConverter converter;
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
                    ParameterizedType sourceMapType = FsReflect.getGenericSuperType(sourceType, Map.class);
                    if (sourceMapType == null) {
                        throw new IllegalArgumentException("Not a map type: " + sourceType + ".");
                    }
                    Type[] sourceActualTypes = sourceMapType.getActualTypeArguments();
                    Type sourceKeyType = sourceActualTypes[0];
                    Type sourceValueType = sourceActualTypes[1];
                    if (dest instanceof Map) {
                        ParameterizedType destMapType = FsReflect.getGenericSuperType(destType, Map.class);
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
                        ParameterizedType destMapType = FsReflect.getGenericSuperType(destType, Map.class);
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
                FsConverter converter =
                    this.converter == null ? FsConverter.defaultConverter() : this.converter;
                Function<Object, Object> nameMapper =
                    this.propertyNameMapper == null ? n -> n : this.propertyNameMapper;
                BiPredicate<Object, @Nullable Object> sourceFilter =
                    this.sourcePropertyFilter == null ? (k, v) -> true : this.sourcePropertyFilter;
                BiPredicate<Object, @Nullable Object> destFilter =
                    this.destPropertyFilter == null ? (k, v) -> true : this.destPropertyFilter;
                if (source instanceof Map) {
                    if (dest instanceof Map) {
                        Map<Object, Object> sourceMap = Fs.as(source);
                        Map<Object, Object> destMap = Fs.as(dest);
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
                                newDestKey = FsConverter.getResult(newDestKey);
                            }
                            if (!putIfNotContained && !destMap.containsKey(newDestKey)) {
                                return;
                            }
                            Object newDestValue = tryConvert(value, sourceValueType, destValueType, converter);
                            if (newDestValue == null) {
                                return;
                            } else {
                                newDestValue = FsConverter.getResult(newDestValue);
                            }
                            if (!destFilter.test(newDestKey, newDestValue)) {
                                return;
                            }
                            destMap.put(newDestKey, newDestValue);
                        });
                    } else {
                        FsBeanResolver resolver =
                            this.beanResolver == null ? FsBeanResolver.defaultResolver() : this.beanResolver;
                        Map<Object, Object> sourceMap = Fs.as(source);
                        FsBean destBean = resolver.resolve(destType);
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
                                newDestNameObj = FsConverter.getResult(newDestNameObj);
                            }
                            String newDestName = (String) newDestNameObj;
                            FsProperty destProperty = destBean.getProperty(newDestName);
                            if (destProperty == null || !destProperty.isWriteable()) {
                                return;
                            }
                            Object newDestValue = tryConvert(value, sourceValueType, destProperty.getType(), converter);
                            if (newDestValue == null) {
                                return;
                            } else {
                                newDestValue = FsConverter.getResult(newDestValue);
                            }
                            if (!destFilter.test(newDestName, newDestValue)) {
                                return;
                            }
                            destProperty.set(dest, newDestValue);
                        });
                    }
                } else {
                    FsBeanResolver resolver =
                        this.beanResolver == null ? FsBeanResolver.defaultResolver() : this.beanResolver;
                    FsBean sourceBean = resolver.resolve(sourceType);
                    if (dest instanceof Map) {
                        Map<Object, Object> destMap = Fs.as(dest);
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
                                newDestKey = FsConverter.getResult(newDestKey);
                            }
                            if (!putIfNotContained && !destMap.containsKey(destKey)) {
                                return;
                            }
                            Object newDestValue = tryConvert(sourceValue, sourceProperty.getType(), destValueType, converter);
                            if (newDestValue == null) {
                                return;
                            } else {
                                newDestValue = FsConverter.getResult(newDestValue);
                            }
                            if (!destFilter.test(destKey, newDestValue)) {
                                return;
                            }
                            destMap.put(newDestKey, newDestValue);
                        });
                    } else {
                        FsBean destBean = resolver.resolve(destType);
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
                                newDestNameObj = FsConverter.getResult(newDestNameObj);
                            }
                            String newDestName = (String) newDestNameObj;
                            FsProperty destProperty = destBean.getProperty(newDestName);
                            if (destProperty == null || !destProperty.isWriteable()) {
                                return;
                            }
                            Object newDestValue = tryConvert(sourceValue, sourceProperty.getType(), destProperty.getType(), converter);
                            if (newDestValue == null) {
                                return;
                            } else {
                                newDestValue = FsConverter.getResult(newDestValue);
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
            private Object tryConvert(Object value, Type fromType, Type destType, FsConverter converter) {
                Object newValue = converter.convertType(value, fromType, destType);
                if (newValue == null) {
                    if (throwIfConvertFailed) {
                        throw new FsConvertException(fromType, destType);
                    } else {
                        return null;
                    }
                }
                return newValue;
            }

            @Override
            public FsBeanCopier withBeanResolver(FsBeanResolver resolver) {
                if (Objects.equals(this.beanResolver, resolver)) {
                    return this;
                }
                return FsBeanCopier.super.withBeanResolver(resolver);
            }

            @Override
            public FsBeanCopier withConverter(FsConverter converter) {
                if (Objects.equals(this.converter, converter)) {
                    return this;
                }
                return FsBeanCopier.super.withConverter(converter);
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
