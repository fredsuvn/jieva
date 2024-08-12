package xyz.fslabo.common.mapper;

import lombok.Builder;
import lombok.Getter;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.bean.BeanProvider;
import xyz.fslabo.common.bean.PropertyInfo;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Mapping options for {@link Mapper} and {@link BeanMapper}.
 *
 * @author sunqian
 */
@Builder
@Getter
public class MappingOptions {

    /**
     * Copy level: {@code ASSIGNABLE}.
     * <p>
     * In this level, if target type is assignable from type of source object, the source object will be returned as
     * mapping result. This is similar to shadow copy.
     */
    public static final int COPY_LEVEL_ASSIGNABLE = 1;

    /**
     * Copy level: {@code EQUAL}.
     * <p>
     * In this level, if target type is equal to type of source object, the source object will be returned as
     * mapping result. This is similar to strict shadow copy.
     */
    public static final int COPY_LEVEL_EQUAL = 2;

    /**
     * Copy level: {@code DEEP}.
     * <p>
     * In this level, the mapper should always create a new instance to return as mapping result. This is similar to
     * deep copy.
     */
    public static final int COPY_LEVEL_DEEP = 3;

    /**
     * Option for {@link BeanProvider}, to resolve bean infos if needed.
     * If this option is null, the mapper will use {@link BeanProvider#defaultProvider()}.
     */
    private @Nullable BeanProvider beanProvider;

    /**
     * Option for {@link Mapper}, to map objects in types if needed.
     * For {@link BeanMapper}, names of properties and keys of entries will be mapped by this mapper before finding
     * dest properties or entries.
     * If this option is null, the mapper will use {@link Mapper#defaultMapper()}.
     */
    private @Nullable Mapper mapper;

    /**
     * Ignored names or keys when mapping properties.
     */
    private @Nullable Collection<?> ignored;

    /**
     * Mapper function for property names of bean and keys of map.
     * <p>
     * The function applies 2 arguments, first is object of name/key, second is specified type of first object, and
     * returns mapped name/key. The mapped object may be a {@link Collection}, which means a name/key maps more than one
     * bean property or map entry. If the name/key itself is a {@link Collection}, a {@link Collection} which has
     * singleton element should be returned. If the function returns null, the name/key will be ignored.
     * <p>
     * Note the {@link #getMapper()} option will still valid for the names and keys after mapping by this name mapper.
     */
    private @Nullable BiFunction<Object, Type, @Nullable Object> nameMapper;

    /**
     * Whether the null value should be ignored when mapping.
     * <p>
     * Default is false.
     */
    private boolean ignoreNull;

    /**
     * Whether ignore error when mapping.
     * <p>
     * Default is false.
     */
    private boolean ignoreError;

    /**
     * Whether put the value into dest map if the dest map doesn't contain the value.
     * <p>
     * Default is true.
     */
    private boolean putNew;

    /**
     * Copy level option. This option determines whether a new instance must be created during the mapping process,
     * similar to shallow copy and deep copy. Here are levels:
     * <ul>
     *     <li>{@link #COPY_LEVEL_ASSIGNABLE};</li>
     *     <li>{@link #COPY_LEVEL_EQUAL};</li>
     *     <li>{@link #COPY_LEVEL_DEEP};</li>
     * </ul>
     * <p>
     * Default is {@link #COPY_LEVEL_ASSIGNABLE}.
     */
    private int copyLevel;

    /**
     * Charset option to determine which charset to use for character conversion.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Charset charset;

    /**
     * Charset option to determine which format to use for number conversion.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable NumberFormat numberFormat;

    /**
     * Charset option to determine which format to use for date conversion.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable DateTimeFormatter dateFormat;

    /**
     * Charset function to determine which charset to use for character conversion. This option is typically used in
     * {@link BeanMapper}.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Function<PropertyInfo, Charset> propertyCharset;

    /**
     * Charset function to determine which format to use for number conversion. This option is typically used in
     * {@link BeanMapper}.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Function<PropertyInfo, NumberFormat> propertyNumberFormat;

    /**
     * Charset function to determine which format to use for date conversion. This option is typically used in
     * {@link BeanMapper}.
     * <p>
     * Default is {@code null}.
     */
    private @Nullable Function<PropertyInfo, DateTimeFormatter> propertyDateFormat;
}
