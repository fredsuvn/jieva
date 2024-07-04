package xyz.fslabo.common.reflect;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface is sub and enhanced type of {@link ParameterizedType}.
 *
 * @author fredsuvn
 */
public interface ParamType extends ParameterizedType {

    /**
     * Returns immutable list of {@link #getActualTypeArguments()}.
     *
     * @return immutable list of {@link #getActualTypeArguments()}
     */
    List<Type> getActualTypeArgumentList();

    /**
     * Returns actual type argument at specified index of {@link #getActualTypeArguments()},
     * or null if the index out of bounds.
     *
     * @param index specified index
     * @return actual type argument at specified index of {@link #getActualTypeArguments()},
     * or null if the index out of bounds
     */
    @Nullable
    default Type getActualTypeArgument(int index) {
        List<Type> types = getActualTypeArgumentList();
        if (index < 0 || index >= types.size()) {
            return null;
        }
        return types.get(index);
    }
}
