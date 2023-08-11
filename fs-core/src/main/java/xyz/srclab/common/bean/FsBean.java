package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.base.FsCase;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.cache.FsCache;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Bean info of object, usually resolved by {@link FsBeanResolver}.
 *
 * @author fredsuvn
 * @see FsBeanResolver
 * @see FsBeanCopier
 */
@ThreadSafe
public interface FsBean {

    /**
     * Returns type of this bean.
     */
    Type getType();

    /**
     * Returns raw type of this bean.
     */
    default Class<?> getRawType() {
        return FsType.getRawType(getType());
    }

    /**
     * Returns all properties in this bean.
     */
    Map<String, FsProperty> getProperties();

    /**
     * Returns property with given name in this bean.
     *
     * @param name given name
     */
    @Nullable
    default FsProperty getProperty(String name) {
        return getProperties().get(name);
    }
}
