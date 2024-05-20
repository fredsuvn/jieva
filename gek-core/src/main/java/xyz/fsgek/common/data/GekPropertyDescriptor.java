package xyz.fsgek.common.data;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;

import java.util.Objects;

/**
 * Descriptor of <b>property</b> of <b>data object</b>.
 * <p>
 * Property of data object is similar to property of Java Bean, but not exactly the same.
 * A data property has a name and value which are accessed by getter and setter. The descriptor indicates its structure
 * from {@link GekDataDescriptor#getProperty(String)} and {@link GekDataDescriptor#getProperties()}.
 *
 * @author fredsuvn
 * @see GekDataDescriptor
 */
@Immutable
@ThreadSafe
public interface GekPropertyDescriptor extends GekPropertyBase {

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link GekPropertyDescriptor}.
     * This method compares result of {@link Object#getClass()}, {@link GekPropertyDescriptor#getName()} and
     * {@link GekPropertyDescriptor#getOwner()}. The code is similar to the following:
     * <pre>
     *     return Objects.equals(descriptor.getClass(), other.getClass())
     *         && Objects.equals(descriptor.getName(), other.getName())
     *         && Objects.equals(descriptor.getOwner(), other.getOwner());
     * </pre>
     * And it works in conjunction with {@link #hashCode(GekPropertyDescriptor)}.
     *
     * @param descriptor descriptor to be compared
     * @param o          object to be compared
     * @return true if equals false otherwise
     */
    static boolean equals(GekPropertyDescriptor descriptor, @Nullable Object o) {
        if (descriptor == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!descriptor.getClass().equals(o.getClass())) {
            return false;
        }
        GekPropertyDescriptor other = (GekPropertyDescriptor) o;
        return Objects.equals(descriptor.getName(), other.getName())
            && Objects.equals(descriptor.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link GekPropertyDescriptor}.
     * This method uses {@link Object#hashCode()} of {@link GekPropertyDescriptor#getName()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return descriptor.getName().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(GekPropertyDescriptor, Object)}.
     *
     * @param descriptor descriptor to be hashed
     * @return hash code of given descriptor
     */
    static int hashCode(GekPropertyDescriptor descriptor) {
        return descriptor.getName().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link GekPropertyDescriptor}.
     * The code is similar to the following:
     * <pre>
     *     return descriptor.getOwner().getType().getTypeName() + "." + descriptor.getName()
     *         + "[" + descriptor.getType().getTypeName() + "]";
     * </pre>
     *
     * @param descriptor descriptor to be string description
     * @return a string description for given descriptor
     */
    static String toString(GekPropertyDescriptor descriptor) {
        return descriptor.getOwner().getType().getTypeName() + "." + descriptor.getName()
            + "[" + descriptor.getType().getTypeName() + "]";
    }

    /**
     * Returns owner data descriptor of this property descriptor.
     *
     * @return owner data descriptor of this property descriptor
     */
    GekDataDescriptor getOwner();
}
