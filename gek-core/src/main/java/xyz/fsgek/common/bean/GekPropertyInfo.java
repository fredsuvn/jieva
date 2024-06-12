package xyz.fsgek.common.bean;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.ThreadSafe;

import java.util.Objects;

/**
 * Information about the property of {@link GekBeanInfo}, commonly using {@link GekBeanInfo#getProperty(String)} or
 * {@link GekBeanInfo#getProperties()} or {@link GekBeanInfo#getProperties()} to get the instance.
 *
 * @author fredsuvn
 * @see GekBeanInfo
 */
@Immutable
@ThreadSafe
public interface GekPropertyInfo extends GekMemberInfo, GekPropertyBase {

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link GekPropertyInfo}.
     * This method compares result of {@link Object#getClass()}, {@link GekPropertyInfo#getName()} and
     * {@link GekPropertyInfo#getOwner()}. The code is similar to the following:
     * <pre>
     *     return Objects.equals(info.getClass(), other.getClass())
     *         && Objects.equals(info.getName(), other.getName())
     *         && Objects.equals(info.getOwner(), other.getOwner());
     * </pre>
     * And it works in conjunction with {@link #hashCode(GekPropertyInfo)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    static boolean equals(GekPropertyInfo info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        GekPropertyInfo other = (GekPropertyInfo) o;
        return Objects.equals(info.getName(), other.getName())
            && Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link GekPropertyInfo}.
     * This method uses {@link Object#hashCode()} of {@link GekPropertyInfo#getName()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return info.getName().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(GekPropertyInfo, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    static int hashCode(GekPropertyInfo info) {
        return info.getName().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link GekPropertyInfo}.
     * The code is similar to the following:
     * <pre>
     *     return info.getOwner().getType().getTypeName() + "." + info.getName()
     *         + "[" + info.getType().getTypeName() + "]";
     * </pre>
     *
     * @param info info to be string description
     * @return a string description for given info
     */
    static String toString(GekPropertyInfo info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "[" + info.getType().getTypeName() + "]";
    }
}
