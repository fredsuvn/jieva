package xyz.fslabo.common.mapper;

import lombok.Builder;
import lombok.Data;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.bean.BeanProvider;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Options for {@link Mapper} and {@link BeanMapper}.
 *
 * @author sunqian
 */
@Data
@Builder
public class MapperOptions {

    /**
     * Type of source object.
     * If this option is null, the mapper will use {@link Object#getClass()} of source object or {@link Object} if
     * source object is null.
     * This option is typically used for generic types.
     */
    private @Nullable Type sourceType;

    /**
     * Type of dest object for {@link BeanMapper}.
     * If this option is null, the mapper will use {@link Object#getClass()} of dest object.
     * This option is typically used for generic types.
     */
    private @Nullable Type destType;

    /**
     * Option for {@link BeanProvider}.
     * If this option is null, the mapper will use {@link BeanProvider#defaultProvider()}.
     */
    private @Nullable BeanProvider beanProvider;

    /**
     * Option for {@link Mapper}.
     * If this option is null, the mapper will use {@link Mapper#defaultMapper()}.
     */
    private @Nullable Mapper mapper;

    /**
     * Ignored names or keys when mapping properties.
     */
    private @Nullable Collection<?> ignored;

    /**
     * Mapper function of names or keys of properties or values.
     * The function applies a name or key and returns dest name or key, if the function returns null, the name or key
     * will be ignored.
     */
    private @Nullable Function<Object, @Nullable Object> nameMapper;

    /**
     * Whether the null value should be ignored when mapping.
     * Default is false.
     */
    private boolean ignoreNull;

    /**
     * Whether ignore error when mapping.
     * Default is false.
     */
    private boolean ignoreError;

    /**
     * Whether put the value into dest map if the dest map doesn't contain the value.
     * Default is true.
     */
    private boolean putNew;
}
