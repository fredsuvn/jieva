package xyz.fsgik.common.base.obj;

import java.lang.reflect.GenericArrayType;

/**
 * Specified object type of {@link GenericArrayType} for {@link FsObj}.
 *
 * @author fredsuvn
 */
public interface GenericArrayObj<T> extends FsObj<T> {

    /**
     * Returns type of hold object as {@link GenericArrayType}.
     *
     * @return type of hold object as {@link GenericArrayType}
     */
    @Override
    GenericArrayType getType();
}
