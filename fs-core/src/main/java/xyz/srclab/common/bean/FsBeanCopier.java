package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Bean copier, usually used to copy bean properties.
 *
 * @author fredsuvn
 */
public interface FsBeanCopier {

    /**
     * Return default bean copier.
     */
    static FsBeanCopier defaultCopier() {
        return null;
    }

    /**
     * Creates a new object as target object of target type,
     * and copies properties from source object to new target object.
     * The source object will be seen as given source type.
     * <p>
     * If failed to create or copy, return null.
     *
     * @param source     source object
     * @param sourceType given source type
     * @param targetType target type
     */
    @Nullable <T> T copyProperties(Object source, Type sourceType, Type targetType);
}
