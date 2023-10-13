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
     * Returns type of current object as {@link WildcardType}.
     */
    @Override
    WildcardType getType();

    /**
     * Returns upper bound by {@link FsReflect#getUpperBound(WildcardType)}.
     */
    @Nullable
    Type getUpperBound();

    /**
     * Returns upper bound by {@link FsReflect#getLowerBound(WildcardType)}.
     */
    @Nullable
    Type getLowerBound();
}
