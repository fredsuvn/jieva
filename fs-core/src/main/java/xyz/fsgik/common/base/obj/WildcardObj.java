package xyz.fsgik.common.base.obj;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.reflect.FsReflect;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Specified object type of {@link WildcardType} for {@link FsObj}.
 *
 * @author fredsuvn
 */
public interface WildcardObj<T> extends FsObj<T> {

    /**
     * Returns type of hold object as {@link WildcardType}.
     *
     * @return type of hold object as {@link WildcardType}
     */
    @Override
    WildcardType getType();

    /**
     * Returns upper bound by {@link FsReflect#getUpperBound(WildcardType)}.
     *
     * @return upper bound type or null
     */
    @Nullable
    Type getUpperBound();

    /**
     * Returns upper bound by {@link FsReflect#getLowerBound(WildcardType)}.
     *
     * @return lower bound type or null
     */
    @Nullable
    Type getLowerBound();
}
