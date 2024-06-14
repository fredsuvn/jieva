package xyz.fslabo.common.base.obj;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Specified object type of {@link WildcardType} for {@link GekObj}.
 *
 * @author fredsuvn
 */
public interface WildcardObj<T> extends GekObj<T> {

    /**
     * Returns type of hold object as {@link WildcardType}.
     *
     * @return type of hold object as {@link WildcardType}
     */
    @Override
    WildcardType getType();

    /**
     * Returns upper bound by {@link JieReflect#getUpperBound(WildcardType)}.
     *
     * @return upper bound type or null
     */
    @Nullable
    Type getUpperBound();

    /**
     * Returns upper bound by {@link JieReflect#getLowerBound(WildcardType)}.
     *
     * @return lower bound type or null
     */
    @Nullable
    Type getLowerBound();
}
