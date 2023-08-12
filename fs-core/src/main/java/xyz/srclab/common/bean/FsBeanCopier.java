package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.convert.FsConvertException;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Properties copier for {@link FsBean}, to copy properties from source object to dest object.
 * The copier supports object bean of which property name's type is {@link String},
 * and map as bean of which key's type is any type.
 *
 * @author fredsuvn
 */
public interface FsBeanCopier {

    /**
     * Return default bean copier with default options of {@link Builder}.
     */
    static FsBeanCopier defaultCopier() {
        return FsUnsafe.ForBean.DEFAULT_COPIER;
    }

    /**
     * Returns new bean properties copier builder.
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
    default <T> T copyProperties(Object source, Type sourceType, T dest, Type destType) {
        return copyProperties(source, sourceType, dest, destType, FsConverter.defaultConverter());
    }

    /**
     * Copies properties from source object to dest object with specified types and converter.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     * @param converter  specified converter
     */
    <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, FsConverter converter);

    /**
     * Builder for {@link FsBeanCopier}.
     */
    class Builder {

        private @Nullable FsBeanResolver beanResolver;
        private boolean throwIfConvertFailed = false;
        private @Nullable Function<Object, Object> propertyNameMapper;
        private @Nullable BiPredicate<Object, @Nullable Object> sourcePropertyFilter;
        private @Nullable BiPredicate<Object, @Nullable Object> destPropertyFilter;
        private boolean putNotContained = true;

        /**
         * Sets bean resolver. If it is null (default), use {@link FsBeanResolver#defaultResolver()}.
         */
        public Builder beanResolver(@Nullable FsBeanResolver beanResolver) {
            this.beanResolver = beanResolver;
            return this;
        }

        /**
         * Sets whether throw {@link FsConvertException} if conversion failed.
         * Default is false, means ignore failed properties.
         */
        public Builder throwIfConvertFailed(boolean throwIfConvertFailed) {
            this.throwIfConvertFailed = throwIfConvertFailed;
            return this;
        }

        /**
         * Sets property name mapper, to map property names of source object to new property names of dest object.
         * The property will be ignored if new name is null or not found in dest bean.
         * <p>
         * For object bean, property name's type is {@link String};
         * for map as bean, key's type is any type.
         * Whatever, the types before and after the conversion <b>must be equal</b>.
         */
        public Builder propertyNameMapper(@Nullable Function<Object, Object> propertyNameMapper) {
            this.propertyNameMapper = propertyNameMapper;
            return this;
        }

        /**
         * Sets source property filter,
         * the first param is source property name, second is source property value (maybe null).
         * <p>
         * For object bean, property name's type is {@link String};
         * for map as bean, key's type is any type.
         * <p>
         * Only the source properties that pass through this filter (return true) will be copied from.
         */
        public Builder sourcePropertyFilter(@Nullable BiPredicate<Object, @Nullable Object> sourcePropertyFilter) {
            this.sourcePropertyFilter = sourcePropertyFilter;
            return this;
        }

        /**
         * Sets dest property filter,
         * the first param is dest property name, second is dest property value after conversion (maybe null).
         * <p>
         * For object bean, property name's type is {@link String};
         * for map as bean, key's type is any type.
         * <p>
         * Only the dest properties that pass through this filter (return true) will be copied into.
         */
        public Builder destPropertyFilter(@Nullable BiPredicate<Object, @Nullable Object> destPropertyFilter) {
            this.destPropertyFilter = destPropertyFilter;
            return this;
        }

        /**
         * Sets whether put the property into dest map if dest map doesn't contain corresponding property.
         * Default is true.
         */
        public Builder putNotContained(boolean putNotContained) {
            this.putNotContained = putNotContained;
            return this;
        }

        /**
         * Builds copier.
         */
        public FsBeanCopier build() {
            return new FsBeanCopierImpl(
                beanResolver == null ? FsBeanResolver.defaultResolver() : beanResolver,
                throwIfConvertFailed,
                propertyNameMapper == null ? it -> it : propertyNameMapper,
                sourcePropertyFilter == null ? (a, b) -> true : sourcePropertyFilter,
                destPropertyFilter == null ? (a, b) -> true : destPropertyFilter,
                putNotContained
            );
        }

        private static final class FsBeanCopierImpl implements FsBeanCopier {

            private final FsBeanResolver beanResolver;
            private final boolean throwIfConvertFailed;
            private final Function<Object, Object> propertyNameMapper;
            private final BiPredicate<Object, @Nullable Object> sourcePropertyFilter;
            private final BiPredicate<Object, @Nullable Object> destPropertyFilter;
            private final boolean putNotContained;

            private FsBeanCopierImpl(
                FsBeanResolver beanResolver,
                boolean throwIfConvertFailed,
                Function<Object, Object> propertyNameMapper,
                BiPredicate<Object, @Nullable Object> sourcePropertyFilter,
                BiPredicate<Object, @Nullable Object> destPropertyFilter,
                boolean putNotContained
            ) {
                this.beanResolver = beanResolver;
                this.throwIfConvertFailed = throwIfConvertFailed;
                this.propertyNameMapper = propertyNameMapper;
                this.sourcePropertyFilter = sourcePropertyFilter;
                this.destPropertyFilter = destPropertyFilter;
                this.putNotContained = putNotContained;
            }

            @Override
            public <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, FsConverter converter) {
                if (source instanceof Map) {
                    ParameterizedType sourceMapType = FsType.getGenericSuperType(sourceType, Map.class);
                    if (sourceMapType == null) {
                        throw new IllegalArgumentException("Not a map type: " + sourceType + ".");
                    }
                    Type[] sourceActualTypes = sourceMapType.getActualTypeArguments();
                    Type sourceKeyType = sourceActualTypes[0];
                    Type sourceValueType = sourceActualTypes[1];
                    if (dest instanceof Map) {
                        ParameterizedType destMapType = FsType.getGenericSuperType(destType, Map.class);
                        if (destMapType == null) {
                            throw new IllegalArgumentException("Not a map type: " + destType + ".");
                        }
                        Type[] destActualTypes = destMapType.getActualTypeArguments();
                        Type destKeyType = destActualTypes[0];
                        Type destValueType = destActualTypes[1];
                        Map<Object, Object> sourceMap = (Map<Object, Object>) source;
                        Map<Object, Object> destMap = (Map<Object, Object>) dest;
                        sourceMap.forEach((key, value) -> {
                            if (!sourcePropertyFilter.test(key, value)) {
                                return;
                            }
                            Object destKey = propertyNameMapper.apply(key);
                            if (destKey == null) {
                                return;
                            }
                            Object newDestKey = converter.convertObject(destKey, sourceKeyType, destKeyType);
                            if (newDestKey == Fs.RETURN) {
                                if (throwIfConvertFailed) {
                                    throw new FsConvertException(sourceKeyType, destKeyType);
                                } else {
                                    return;
                                }
                            }
                            if (!putNotContained && !destMap.containsKey(newDestKey)) {
                                return;
                            }
                            Object newDestValue = converter.convertObject(value, sourceValueType, destValueType);
                            if (newDestValue == Fs.RETURN) {
                                if (throwIfConvertFailed) {
                                    throw new FsConvertException(sourceValueType, destValueType);
                                } else {
                                    return;
                                }
                            }
                            if (!destPropertyFilter.test(newDestKey, newDestValue)) {
                                return;
                            }
                            destMap.put(newDestKey, newDestValue);
                        });
                    } else {
                        FsBean destBean = beanResolver.resolve(destType);
                        Map<Object, Object> sourceMap = (Map<Object, Object>) source;
                        sourceMap.forEach((key, value) -> {
                            if (!sourcePropertyFilter.test(key, value)) {
                                return;
                            }
                            Object destNameObj = propertyNameMapper.apply(key);
                            if (destNameObj == null) {
                                return;
                            }
                            Object destNameCvt = converter.convertObject(destNameObj, sourceKeyType, String.class);
                            if (destNameCvt == Fs.RETURN) {
                                if (throwIfConvertFailed) {
                                    throw new FsConvertException(sourceKeyType, String.class);
                                } else {
                                    return;
                                }
                            }
                            String destName = (String) destNameCvt;
                            FsProperty destProperty = destBean.getProperty(destName);
                            if (destProperty == null || !destProperty.isWriteable()) {
                                return;
                            }
                            Object newDestValue = converter.convertObject(value, sourceValueType, destProperty.getType());
                            if (newDestValue == Fs.RETURN) {
                                if (throwIfConvertFailed) {
                                    throw new FsConvertException(sourceValueType, destProperty.getType());
                                } else {
                                    return;
                                }
                            }
                            if (!destPropertyFilter.test(destName, newDestValue)) {
                                return;
                            }
                            destProperty.set(dest, newDestValue);
                        });
                    }
                } else {
                    FsBean sourceBean = beanResolver.resolve(sourceType);
                    if (dest instanceof Map) {
                        ParameterizedType destMapType = FsType.getGenericSuperType(destType, Map.class);
                        if (destMapType == null) {
                            throw new IllegalArgumentException("Not a map type: " + destType + ".");
                        }
                        Type[] destActualTypes = destMapType.getActualTypeArguments();
                        Type destKeyType = destActualTypes[0];
                        Type destValueType = destActualTypes[1];
                        Map<Object, Object> destMap = (Map<Object, Object>) dest;
                        sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                            if (!sourceProperty.isReadable()) {
                                return;
                            }
                            Object sourceValue = sourceProperty.get(source);
                            if (!sourcePropertyFilter.test(sourceName, sourceValue)) {
                                return;
                            }
                            Object destKey = propertyNameMapper.apply(sourceName);
                            if (destKey == null) {
                                return;
                            }
                            Object newDestKey = converter.convertObject(destKey, sourceProperty.getType(), destKeyType);
                            if (newDestKey == Fs.RETURN) {
                                if (throwIfConvertFailed) {
                                    throw new FsConvertException(sourceProperty.getType(), destKeyType);
                                } else {
                                    return;
                                }
                            }
                            if (!putNotContained && !destMap.containsKey(newDestKey)) {
                                return;
                            }
                            Object newDestValue = converter.convertObject(sourceValue, sourceProperty.getType(), destValueType);
                            if (newDestValue == Fs.RETURN) {
                                if (throwIfConvertFailed) {
                                    throw new FsConvertException(sourceProperty.getType(), destValueType);
                                } else {
                                    return;
                                }
                            }
                            if (!destPropertyFilter.test(newDestKey, newDestValue)) {
                                return;
                            }
                            destMap.put(newDestKey, newDestValue);
                        });
                    } else {
                        FsBean destBean = beanResolver.resolve(destType);
                        sourceBean.getProperties().forEach((sourceName, sourceProperty) -> {
                            if (!sourceProperty.isReadable()) {
                                return;
                            }
                            Object sourceValue = sourceProperty.get(source);
                            if (!sourcePropertyFilter.test(sourceName, sourceValue)) {
                                return;
                            }
                            Object destNameObj = propertyNameMapper.apply(sourceName);
                            if (destNameObj == null) {
                                return;
                            }
                            Object destNameCvt = converter.convertObject(destNameObj, sourceProperty.getType(), String.class);
                            if (destNameCvt == Fs.RETURN) {
                                if (throwIfConvertFailed) {
                                    throw new FsConvertException(sourceProperty.getType(), String.class);
                                } else {
                                    return;
                                }
                            }
                            String destName = (String) destNameCvt;
                            FsProperty destProperty = destBean.getProperty(destName);
                            if (destProperty == null || !destProperty.isWriteable()) {
                                return;
                            }
                            Object newDestValue = converter.convertObject(
                                sourceValue, sourceProperty.getType(), destProperty.getType());
                            if (newDestValue == Fs.RETURN) {
                                if (throwIfConvertFailed) {
                                    throw new FsConvertException(sourceProperty.getType(), destProperty.getType());
                                } else {
                                    return;
                                }
                            }
                            if (!destPropertyFilter.test(destName, newDestValue)) {
                                return;
                            }
                            destProperty.set(dest, newDestValue);
                        });
                    }
                }
                return dest;
            }
        }
    }
}
