package xyz.fslabo.common.mapper;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.bean.BeanProvider;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.function.BiFunction;

/**
 * Context in mapping process of {@link BeanMapper} and {@link Mapper}.
 *
 * @author fredsuvn
 */
public interface MappingContext {

    /**
     * Option for {@link BeanProvider}, to resolve bean infos if needed.
     * If this option is null, the mapper will use {@link BeanProvider#defaultProvider()}.
     */
    @Nullable
    BeanProvider beanProvider();

    /**
     * Option for {@link Mapper}, to map objects in types if needed.
     * For {@link BeanMapper}, names of properties and keys of entries will be mapped by this mapper before finding
     * dest properties or entries.
     * If this option is null, the mapper will use {@link Mapper#defaultMapper()}.
     */
     @Nullable Mapper mapper;

    /**
     * Ignored names or keys when mapping properties.
     */
     @Nullable Collection<?> ignored;

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
     @Nullable BiFunction<Object, Type, @Nullable Object> nameMapper;

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
     * Whether to use deep copy for mapping with {@link BeanMapper}. For {@link Mapper}, if this option is true and
     * target mapping type is mutable, the mapper will create a new instance to return.
     * <p>
     * Default is false (shallow copy).
     */
    private boolean deepCopy;

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
}
