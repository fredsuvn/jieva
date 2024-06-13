package xyz.fslabo.common.base.obj;

import java.lang.reflect.GenericArrayType;

/**
 * Specified object type of {@link GenericArrayType} for {@link GekObj}.
 *
 * @author fredsuvn
 */
public interface GenericArrayObj<T> extends GekObj<T> {

    /**
     * Returns type of hold object as {@link GenericArrayType}.
     *
     * @return type of hold object as {@link GenericArrayType}
     */
    @Override
    GenericArrayType getType();
}
