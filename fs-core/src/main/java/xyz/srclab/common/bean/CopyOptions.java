package xyz.srclab.common.bean;

import lombok.Builder;
import lombok.Getter;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertException;
import xyz.srclab.common.convert.FsConverter;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Options for bean copy operation.
 *
 * @author fredsuvn
 */
@Builder
@Getter
public class CopyOptions {

    /**
     * Default options.
     */
    public static final CopyOptions DEFAULT = CopyOptions.builder().build();

    /**
     * Specified bean resolver. If it is null (default), use {@link FsBeanResolver#defaultResolver()}.
     */
    private @Nullable FsBeanResolver beanResolver;
    /**
     * Specified object converter. If it is null (default), use {@link FsConverter#defaultConverter()}.
     */
    private @Nullable FsConverter converter;
    /**
     * Whether throws {@link FsConvertException} if conversion operation failed.
     * Default is false, means ignore failed properties.
     */
    @Builder.Default
    private boolean throwIfConvertFailed = false;
    /**
     * Property name mapper, to map property names from source object to dest object.
     * The property will be ignored if new name is null or not found in dest bean.
     * <p>
     * For object bean, type of property names is always {@link String};
     * for map-wrapped bean, type of keys is any type.
     */
    private @Nullable Function<Object, Object> propertyNameMapper;
    /**
     * Source property filter,
     * the first param is name of source property, second is value of source property value.
     * <p>
     * Only the property that pass through this filter (return true) will be copied from.
     */
    private @Nullable BiPredicate<Object, @Nullable Object> sourcePropertyFilter;
    /**
     * Dest property filter,
     * the first param is name of dest property, second is converted value of source property (maybe null)
     * that is prepared to copy.
     * <p>
     * Only the property that pass through this filter (return true) will be copied from.
     */
    private @Nullable BiPredicate<Object, @Nullable Object> destPropertyFilter;
    /**
     * Whether put the property into dest map if dest map doesn't contain corresponding property.
     * Default is true.
     */
    @Builder.Default
    private boolean putIfNotContained = true;

    /**
     * Copies current setting into a new builder and returns.
     */
    public CopyOptionsBuilder toBuilder() {
        return CopyOptions.builder()
            .beanResolver(beanResolver)
            .converter(converter)
            .throwIfConvertFailed(throwIfConvertFailed)
            .propertyNameMapper(propertyNameMapper)
            .sourcePropertyFilter(sourcePropertyFilter)
            .destPropertyFilter(destPropertyFilter)
            .putIfNotContained(putIfNotContained);
    }
}
