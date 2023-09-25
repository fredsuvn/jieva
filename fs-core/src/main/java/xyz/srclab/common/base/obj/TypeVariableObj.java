package xyz.srclab.common.base.obj;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * Specified object type of {@link TypeVariable} for {@link FsObj}.
 *
 * @author fredsuvn
 */
public interface TypeVariableObj<T> extends FsObj<T> {

    /**
     * Returns type of current object as {@link TypeVariable}.
     */
    @Override
    TypeVariable<?> getType();

    /**
     * Returns a list of {@link Type} objects representing the upper bound(s) of this type variable.
     * The list comes from {@link TypeVariable#getBounds()}.
     */
    List<Type> getBounds();

    /**
     * Returns {@link Type} at index of {@link #getBounds()}.
     */
    default Type getBound(int index) {
        return getBounds().get(index);
    }

    /**
     * Returns a list of {@link Type} objects that represent the use of types to denote the upper bounds of
     * the type parameter represented by this TypeVariable.
     * The list comes from {@link TypeVariable#getAnnotatedBounds()}.
     */
    List<AnnotatedType> getAnnotatedBounds();

    /**
     * Returns {@link Type} at index of {@link #getAnnotatedBounds()}.
     */
    default AnnotatedType getAnnotatedBound(int index) {
        return getAnnotatedBounds().get(index);
    }
}
