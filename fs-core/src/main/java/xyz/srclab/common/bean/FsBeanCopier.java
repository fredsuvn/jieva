package xyz.srclab.common.bean;

import java.lang.reflect.Type;

/**
 * Properties copier for {@link FsBean}, to copy properties from source object to dest object.
 * The copier supports object bean of which type of property names is {@link String},
 * and map-wrapped bean of which type of keys is any type.
 *
 * @author fredsuvn
 */
public interface FsBeanCopier {

    /**
     * Return default bean copier.
     */
    static FsBeanCopier defaultCopier() {
        return BeanCopierImpl.INSTANCE;
    }

    /**
     * Copies properties from source object to dest object.
     *
     * @param source source object
     * @param dest   dest object
     */
    default <T> T copyProperties(Object source, T dest) {
        return copyProperties(source, source.getClass(), dest, dest.getClass());
    }

    /**
     * Copies properties from source object to dest object with specified options.
     *
     * @param source  source object
     * @param dest    dest object
     * @param options specified options
     */
    default <T> T copyProperties(Object source, T dest, CopyOptions options) {
        return copyProperties(source, source.getClass(), dest, dest.getClass(), options);
    }

    /**
     * Copies properties from source object to dest object with specified types.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     */
    default <T> T copyProperties(Object source, Type sourceType, T dest, Type destType) {
        return copyProperties(source, sourceType, dest, destType, CopyOptions.DEFAULT);
    }

    /**
     * Copies properties from source object to dest object with specified types and options.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     * @param options    specified options
     */
    <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, CopyOptions options);
}
