package xyz.fsgek.common.base.obj;

/**
 * Specified object type of {@link Class} for {@link FsObj}.
 *
 * @author fredsuvn
 */
public interface ClassObj<T> extends FsObj<T> {

    /**
     * Returns type of hold object as {@link Class}.
     *
     * @return type of hold object as {@link Class}
     */
    @Override
    Class<T> getType();
}
