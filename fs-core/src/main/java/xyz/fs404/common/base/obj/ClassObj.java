package xyz.fs404.common.base.obj;

/**
 * Specified object type of {@link Class} for {@link FsObj}.
 *
 * @author fredsuvn
 */
public interface ClassObj<T> extends FsObj<T> {

    /**
     * Returns type of current object as {@link Class}.
     */
    @Override
    Class<T> getType();
}
