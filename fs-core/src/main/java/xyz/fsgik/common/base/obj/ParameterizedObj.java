package xyz.fsgik.common.base.obj;

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
     * Returns type of hold object as {@link ParameterizedType}.
     *
     * @return type of hold object as {@link ParameterizedType}
     */
    @Override
    ParameterizedType getType();

    /**
     * Returns a list of {@link Type} objects representing the actual type arguments to this type.
     * The list comes from {@link ParameterizedType#getActualTypeArguments()}.
     *
     * @return actual type arguments
     */
    List<Type> getActualTypeArguments();

    /**
     * Returns {@link Type} at specified index of {@link #getActualTypeArguments()}.
     *
     * @param index specified index
     * @return actual type argument at specified index
     */
    default Type getActualTypeArgument(int index) {
        return getActualTypeArguments().get(index);
    }
}
