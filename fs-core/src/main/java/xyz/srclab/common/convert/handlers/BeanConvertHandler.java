package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;

import static xyz.srclab.common.convert.FsConverter.Handler;

/**
 * Convert handler implementation which is used to support the conversion of bean types with
 * {@link xyz.srclab.common.bean.FsBean.Copier#copyProperties(Object, Type)}.
 * <p>
 * This handler is system default suffix handler (with {@link #BeanConvertHandler()}),
 * any object will be seen as "bean", and the conversion means create new object and copy properties.
 * <p>
 * Note if the {@code obj} is null, return {@link Fs#CONTINUE}.
 *
 * @author fredsuvn
 */
public class BeanConvertHandler implements Handler {

    // private final FsBean.Copier copier;

    /**
     * Constructs with default bean resolver and copier:
     * <ul>
     *     <li>{@link xyz.srclab.common.bean.FsBean#defaultCopier()}</li>
     * </ul>
     */
    public BeanConvertHandler() {
        // this(FsBean.defaultCopier());
    }

    /**
     * Constructs with given bean copier.
     *
     * @param copier given bean copier
     */
    // public BeanConvertHandler(FsBean.Copier copier) {
    //     this.copier = copier;
    // }
    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options, FsConverter converter) {
        if (source == null) {
            return Fs.CONTINUE;
        }
        // Object result = copier.copyProperties(source, targetType);
        // if (result == null) {
        //     return CONTINUE;
        // }
        // return result;
        return Fs.CONTINUE;
    }
}
