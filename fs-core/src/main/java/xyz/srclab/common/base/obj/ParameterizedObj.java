package xyz.srclab.common.base.obj;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Specified object type of {@link ParameterizedType} for {@link FsObj}.
 *
 * @author fredsuvn
 */
public interface ParameterizedObj<T> extends FsObj<T> {

    /**
     * Returns type of current object as {@link ParameterizedType}.
     */
    @Override
    ParameterizedType getType();

    /**
     * Returns a list of {@link Type} objects representing the actual type arguments to this type.
     * The list comes from {@link ParameterizedType#getActualTypeArguments()}.
     */
    List<Type> getActualTypeArguments();

    /**
     * Returns {@link Type} at index of {@link #getActualTypeArguments()}.
     */
    default Type getActualTypeArgument(int index) {
        return getActualTypeArguments().get(index);
    }
}
