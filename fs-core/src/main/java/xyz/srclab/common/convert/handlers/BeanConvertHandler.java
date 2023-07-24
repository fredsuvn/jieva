package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.bean.FsBean;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;

import static xyz.srclab.common.convert.FsConverter.CONTINUE;
import static xyz.srclab.common.convert.FsConverter.Handler;

/**
 * Convert handler implementation which is used to support the conversion of bean types with
 * {@link FsBean#copyProperties(Object, Type, Type, FsBeanResolver, FsConverter)}.
 * This handler is system default suffix handler (with {@link #BeanConvertHandler()}),
 * any object will be seen as "bean", and the conversion means copy properties.
 * <p>
 * Note if the {@code obj} is null, return {@link FsConverter#CONTINUE}.
 *
 * @author fredsuvn
 */
public class BeanConvertHandler implements Handler {

    private final FsBeanResolver resolver;

    /**
     * Constructs with default bean resolver: {@link FsBeanResolver#defaultResolver()}.
     */
    public BeanConvertHandler() {
        this(FsBeanResolver.defaultResolver());
    }

    /**
     * Constructs with given bean resolver.
     *
     * @param resolver given bean resolver
     */
    public BeanConvertHandler(FsBeanResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
        if (source == null) {
            return CONTINUE;
        }
        Object result = FsBean.copyProperties(source, sourceType, targetType, resolver, converter);
        if (result == null) {
            return CONTINUE;
        }
        return result;
    }
}
