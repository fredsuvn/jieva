package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;

import java.util.Map;

/**
 * Bean info for java object.
 *
 * @author sunq62
 */
public interface FsBean {

    /**
     * Returns all properties in this bean.
     */
    Map<String, FsBeanProperty> getProperties();

    /**
     * Returns property with given name in this bean.
     *
     * @param name given name
     */
    @Nullable
    default FsBeanProperty getProperty(String name) {
        return getProperties().get(name);
    }
}
