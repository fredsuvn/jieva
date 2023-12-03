package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;

import java.util.Objects;

/**
 * Represents property of ({@link GekBean}).
 *
 * @author fredsuvn
 */
@Immutable
@ThreadSafe
public interface GekProperty extends GekPropertyBase {

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for this interface.
     * This method uses result of {@link GekProperty#getName()} and {@link GekProperty#getOwner()} to compare,
     * and work in conjunction with {@link #hashCode(GekProperty)}.
     *
     * @param property comparing property
     * @param o        object to be compared
     * @return true if equal false otherwise
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
     * This method uses hash of {@link GekProperty#getName()} to compute,
     * and work in conjunction with {@link #equals(GekProperty, Object)}.
     *
     * @param property property to be hashed
     * @return hash code of given property
     */
    static int hashCode(GekProperty property) {
        return Objects.hash(property.getName());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for this interface.
     *
     * @param property property to be stringified
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
