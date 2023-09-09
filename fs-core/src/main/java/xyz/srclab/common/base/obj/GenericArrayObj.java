package xyz.srclab.common.base.obj;

import java.lang.reflect.GenericArrayType;

/**
 * Specified object type of {@link GenericArrayType} for {@link FsObj}.
 *
 * @author fredsuvn
 */
public interface GenericArrayObj<T> extends FsObj<T> {

    /**
     * Returns type of current object as {@link GenericArrayType}.
     */
    @Override
    GenericArrayType getType();
}
