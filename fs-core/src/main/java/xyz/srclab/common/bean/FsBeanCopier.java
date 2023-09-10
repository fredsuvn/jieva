package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConvertException;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Properties copier for {@link FsBean}, to copy properties from source object to dest object.
 * The copier supports both common object sa bean and {@link Map}-wrapped bean.
 *
 * @author fredsuvn
 * @see Options
 */
public interface FsBeanCopier {

    /**
     * Return default bean copier.
     */
    static FsBeanCopier defaultCopier() {
        return BeanCopierImpl.INSTANCE;
    }

    /**
     * Returns default options.
     */
    static Options defaultOptions() {
        return Options.Builder.DEFAULT;
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
     * Copies properties from source object to dest object with specified options.
     *
     * @param source  source object
     * @param dest    dest object
     * @param options specified options
     */
    default <T> T copyProperties(Object source, T dest, Options options) {
        return copyProperties(source, source.getClass(), dest, dest.getClass(), options);
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
        return copyProperties(source, sourceType, dest, destType, defaultOptions());
    }

    /**
     * Copies properties from source object to dest object with specified types and options.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     * @param options    specified options
     */
    <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, Options options);

    /**
     * Options for bean copy operation.
     *
     * @author fredsuvn
     */
    interface Options {

        /**
         * Returns a new builder for {@link Options}.
         */
        static Builder newBuilder() {
            return new Builder();
        }

        /**
         * Returns bean resolver for copy operation.
         * Default is null, in this case the operation will use {@link FsBeanResolver#defaultResolver()}.
         */
        @Nullable
        FsBeanResolver beanResolver();

        /**
         * Returns object converter for copy operation.
         * Default is null, in this case the operation will use {@link FsConverter#defaultConverter()}.
         */
        @Nullable
        FsConverter converter();

        /**
         * Returns whether throws {@link FsConvertException} if conversion operation was failed.
         * Default is false, means ignore failed properties.
         */
        boolean throwIfConvertFailed();

        /**
         * Returns property name mapper, to map property names from source object to dest object.
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
        @Nullable <T> Function<T, T> propertyNameMapper();

        /**
         * Returns source property filter,
         * the first param is name of source property, second is value of source property value.
         * <p>
         * Only the property that pass through this filter (return true) will be copied from.
         */
        @Nullable
        BiPredicate<Object, @Nullable Object> sourcePropertyFilter();

        /**
         * Returns dest property filter,
         * the first param is name of dest property, second is converted value of source property (maybe null)
         * that is prepared to copy.
         * <p>
         * Only the property that pass through this filter (return true) will be copied from.
         */
        @Nullable
        BiPredicate<Object, @Nullable Object> destPropertyFilter();

        /**
         * Returns whether put the property into dest map if dest map doesn't contain corresponding property.
         * Default is true.
         */
        boolean putIfNotContained();

        /**
         * Returns a new builder with current options.
         */
        default Builder toBuilder() {
            return newBuilder()
                .beanResolver(beanResolver())
                .converter(converter())
                .throwIfConvertFailed(throwIfConvertFailed())
                .propertyNameMapper(propertyNameMapper())
                .sourcePropertyFilter(sourcePropertyFilter())
                .destPropertyFilter(destPropertyFilter())
                .putIfNotContained(putIfNotContained());
        }

        /**
         * Builder for {@link Options}.
         */
        class Builder {

            private static final Options DEFAULT = new Builder().build();

            private FsBeanResolver beanResolver;
            private FsConverter converter;
            private boolean throwIfConvertFailed = false;
            private Function<Object, Object> propertyNameMapper;
            private BiPredicate<Object, @Nullable Object> sourcePropertyFilter;
            private BiPredicate<Object, @Nullable Object> destPropertyFilter;
            private boolean putIfNotContained = true;

            /**
             * Sets bean resolver.
             * Default is null.
             */
            public Builder beanResolver(FsBeanResolver beanResolver) {
                this.beanResolver = beanResolver;
                return this;
            }

            /**
             * Sets object converter.
             * Default is null.
             */
            public Builder converter(FsConverter converter) {
                this.converter = converter;
                return this;
            }

            /**
             * Sets whether throws {@link FsConvertException} if conversion operation was failed.
             * Default is false.
             */
            public Builder throwIfConvertFailed(boolean throwIfConvertFailed) {
                this.throwIfConvertFailed = throwIfConvertFailed;
                return this;
            }

            /**
             * Sets property name mapper.
             * Default is null.
             */
            public <T> Builder propertyNameMapper(Function<T, T> propertyNameMapper) {
                this.propertyNameMapper = Fs.as(propertyNameMapper);
                return this;
            }

            /**
             * Sets source property filter.
             * Default is null.
             */
            public Builder sourcePropertyFilter(BiPredicate<Object, @Nullable Object> sourcePropertyFilter) {
                this.sourcePropertyFilter = sourcePropertyFilter;
                return this;
            }

            /**
             * Sets dest property filter.
             * Default is null.
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
             * Builds a new instance of {@link Options}.
             */
            public Options build() {
                return new Options() {

                    private final FsBeanResolver beanResolver = Builder.this.beanResolver;
                    private final FsConverter converter = Builder.this.converter;
                    private final boolean throwIfConvertFailed = Builder.this.throwIfConvertFailed;
                    private final Function<Object, Object> propertyNameMapper = Builder.this.propertyNameMapper;
                    private final BiPredicate<Object, @Nullable Object> sourcePropertyFilter =
                        Builder.this.sourcePropertyFilter;
                    private final BiPredicate<Object, @Nullable Object> destPropertyFilter =
                        Builder.this.destPropertyFilter;
                    private final boolean putIfNotContained = Builder.this.putIfNotContained;


                    @Override
                    public @Nullable FsBeanResolver beanResolver() {
                        return beanResolver;
                    }

                    @Override
                    public @Nullable FsConverter converter() {
                        return converter;
                    }

                    @Override
                    public boolean throwIfConvertFailed() {
                        return throwIfConvertFailed;
                    }

                    @Override
                    public @Nullable <T> Function<T, T> propertyNameMapper() {
                        return Fs.as(propertyNameMapper);
                    }

                    @Override
                    public @Nullable BiPredicate<Object, @Nullable Object> sourcePropertyFilter() {
                        return sourcePropertyFilter;
                    }

                    @Override
                    public @Nullable BiPredicate<Object, @Nullable Object> destPropertyFilter() {
                        return destPropertyFilter;
                    }

                    @Override
                    public boolean putIfNotContained() {
                        return putIfNotContained;
                    }
                };
            }
        }
    }
}
