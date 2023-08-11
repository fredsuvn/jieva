// package xyz.srclab.common.bean;
//
// import xyz.srclab.annotations.Nullable;
// import xyz.srclab.common.convert.FsConvertException;
// import xyz.srclab.common.convert.FsConverter;
//
// import java.lang.reflect.Type;
// import java.util.function.BiPredicate;
// import java.util.function.Function;
//
// /**
//  * Properties copier for {@link FsBean}, to copy properties from source object to dest object.
//  *
//  * @author fredsuvn
//  */
// public interface FsBeanCopier {
//
//     /**
//      * Return default bean copier.
//      */
//     static FsBeanCopier defaultCopier() {
//         return null;
//     }
//
//     /**
//      * Conversion fail policy: ignore failed property.
//      */
//     int IGNORE = 1;
//
//     /**
//      * Conversion fail policy: throw {@link FsConvertException}.
//      */
//     int THROW = 2;
//
//     /**
//      * Copies properties from source object to dest object.
//      *
//      * @param source source object
//      * @param dest   dest object
//      */
//     default <T> T copyProperties(Object source, T dest) {
//         return copyProperties(source, source.getClass(), dest, dest.getClass());
//     }
//
//     /**
//      * Copies properties from source object to dest object.
//      *
//      * @param source     source object
//      * @param sourceType type of source object
//      * @param dest       dest object
//      * @param destType   type of dest object
//      */
//     <T> T copyProperties(Object source, Type sourceType, T dest, Type destType);
//
//     /**
//      * Builder for {@link FsBeanCopier}.
//      */
//     class Builder {
//
//         private @Nullable FsBeanResolver beanResolver;
//         private @Nullable FsConverter converter;
//         private @Nullable Function<? super String, ? extends String> propertyNameMapper;
//         private @Nullable BiPredicate<FsProperty, FsProperty> propertyFilter;
//         // private
//
//         /**
//          * Sets bean resolver. If it is null, use {@link FsBeanResolver#defaultResolver()}.
//          */
//         Builder beanResolver(@Nullable FsBeanResolver beanResolver) {
//             this.beanResolver = beanResolver;
//             return this;
//         }
//
//         /**
//          * Sets object converter. If it is null, use {@link FsConverter#defaultConverter()}.
//          */
//         Builder converter(@Nullable FsConverter converter) {
//             this.converter = converter;
//             return this;
//         }
//
//         /**
//          * Sets property name mapper, to map property names of source object to new property names of dest object.
//          * If the new property name is null, copy of that property will be ignored.
//          */
//         Builder propertyNameMapper(@Nullable Function<? super String, ? extends String> propertyNameMapper) {
//             this.propertyNameMapper = propertyNameMapper;
//             return this;
//         }
//
//         /**
//          * Sets property filter, the first property is from source object, second from dest object.
//          * Only the properties that pass through this filter (return true) will be copied.
//          */
//         Builder propertyFilter(@Nullable BiPredicate<FsProperty, FsProperty> propertyFilter) {
//             this.propertyFilter = propertyFilter;
//             return this;
//         }
//
//         /**
//          * Sets dest value filter.
//          * The first object is dest value (after converting), second is dest property.
//          * <p>
//          * If dest value filter is not null,
//          * only the values that pass through this filter (return true, including null) will be set into corresponding
//          * dest property.
//          * <p>
//          * If dest value filter is null,
//          * only the non-null values will be set into corresponding dest property.
//          */
//         Builder destValueFilter(@Nullable BiPredicate<@Nullable Object, FsProperty> destValueFilter) {
//             // this.destValueFilter = destValueFilter;
//             return this;
//         }
//
//         /**
//          * Sets object generator to generate new instance for dest bean type.
//          * <p>
//          * If this generator is null, the copier will use empty constructor to generate new instance.
//          */
//         Builder objectGenerator(@Nullable Function<Type, Object> objectGenerator) {
//             // this.objectGenerator = objectGenerator;
//             return this;
//         }
//
//         /**
//          * Builds copier.
//          */
//         Copier build() {
//             return new CopierImpl(
//                 beanResolver, null, null, propertyFilter, null, 1);
//         }
//
//         private final class CopierImpl implements Copier {
//
//             private final Resolver beanResolver;
//             private final @Nullable Function<CharSequence, String> nameConverter;
//             private final @Nullable FsConverter valueConverter;
//             private final @Nullable BiPredicate<FsProperty, FsProperty> propertyFilter;
//             private final @Nullable BiPredicate<@Nullable Object, FsProperty> destValueFilter;
//             private final int conversionFailPolicy;
//
//             private CopierImpl(
//                 Resolver beanResolver,
//                 @Nullable Function<CharSequence, String> nameConverter,
//                 @Nullable FsConverter valueConverter,
//                 @Nullable BiPredicate<FsProperty, FsProperty> propertyFilter,
//                 @Nullable BiPredicate<@Nullable Object, FsProperty> destValueFilter,
//                 int conversionFailPolicy
//             ) {
//                 this.beanResolver = beanResolver;
//                 this.nameConverter = nameConverter;
//                 this.valueConverter = valueConverter;
//                 this.propertyFilter = propertyFilter;
//                 this.destValueFilter = destValueFilter;
//                 this.conversionFailPolicy = conversionFailPolicy;
//             }
//
//             @Override
//             public <T> T copyProperties(Object source, Type sourceType, T dest, Type destType) {
//                 // FsBean sourceBean = (source instanceof Map) ?
//                 //     beanResolver.wrapMap((Map<?, ?>) source, sourceType) : beanResolver.resolve(sourceType);
//                 // FsBean destBean = (dest instanceof Map) ?
//                 //     beanResolver.wrapMap((Map<?, ?>) dest, destType) : beanResolver.resolve(destType);
//                 // sourceBean.getProperties().forEach((name, srcProperty) -> {
//                 //     String destPropertyName = nameConverter == null ? name : nameConverter.apply(name);
//                 //     if (destPropertyName == null) {
//                 //         return;
//                 //     }
//                 //     FsBeanProperty destProperty = destBean.getProperty(destPropertyName);
//                 //     if (destProperty == null) {
//                 //         return;
//                 //     }
//                 //     if (propertyFilter != null && !propertyFilter.test(srcProperty, destProperty)) {
//                 //         return;
//                 //     }
//                 //     Object destValue;
//                 //     if (valueConverter != null) {
//                 //         destValue = valueConverter.convert(
//                 //             srcProperty.get(source), srcProperty.getType(), destProperty.getType(), valueConverter.getOptions());
//                 //         if (destValue == FsConverter.UNSUPPORTED) {
//                 //             // throw new UnsupportedConvertException()
//                 //         }
//                 //     } else {
//                 //         if (Objects.equals(srcProperty.getType(), destProperty.getType())) {
//                 //             destValue = srcProperty.get(source);
//                 //         } else {
//                 //             return;
//                 //         }
//                 //     }
//                 //     if (destValueFilter != null && !destValueFilter.test(destValue, destProperty)) {
//                 //         return;
//                 //     }
//                 //     destProperty.set(dest, destValue);
//                 // });
//                 return null;
//             }
//         }
//     }
// }
