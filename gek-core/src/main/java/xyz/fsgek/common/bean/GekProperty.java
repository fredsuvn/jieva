package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.ThreadSafe;

/**
 * Represents property of a bean ({@link GekBean}).
 *
 * @author fredsuvn
 */
@Immutable
@ThreadSafe
public interface GekProperty extends GekPropertyBase {

    /**
     * Returns a string describes given property.
     *
     * @param property given property
     * @return a string describes given property
     */
    static String toString(GekProperty property) {
        return "property{"
            + "name: " + property.getName() + ", "
            + "type: " + property.getType() + ", "
            + "ownerType: " + property.getOwner().getType()
            + "}";
    }

    /**
     * Returns owner bean of this property.
     *
     * @return owner bean of this property
     */
    GekBean getOwner();
}
