package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.Gek;

import java.util.Objects;

/**
 * This interface represents property of {@link GekBean}.
 * It should be immutable and thread-safe.
 *
 * @author fredsuvn
 */
@Immutable
@ThreadSafe
public interface GekProperty extends GekPropertyBase {

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for this interface.
     * This method uses result of {@link GekProperty#getName()} and {@link GekProperty#getOwner()} to compare:
     * <pre>
     *     return Objects.equals(property.getName(), other.getName())
     *         && Objects.equals(property.getOwner(), other.getOwner());
     * </pre>
     * And it works in conjunction with {@link #hashCode(GekProperty)}.
     *
     * @param property comparing property
     * @param o        object to be compared
     * @return true if equals false otherwise
     */
    static boolean equals(GekProperty property, @Nullable Object o) {
        if (property == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!property.getClass().equals(o.getClass())) {
            return false;
        }
        GekProperty other = (GekProperty) o;
        return Objects.equals(property.getName(), other.getName())
            && Objects.equals(property.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for this interface.
     * This method uses {@link Object#hashCode()} of {@link GekProperty#getName()} to compute:
     * <pre>
     *     return property.getName().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(GekProperty, Object)}.
     *
     * @param property property to be hashed
     * @return hash code of given property
     */
    static int hashCode(GekProperty property) {
        return property.getName().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for this interface.
     *
     * @param property property to be stringified
     * @return a string describes given property
     */
    static String toString(GekProperty property) {
        return "{@objId: " + Gek.systemHash(property) + ", "
            + "@objType: property, "
            + "@objClass: " + property.getClass().getName() + ", "
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
