package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Information about the method of {@link GekBeanInfo}, commonly using {@link GekBeanInfo#getMethods()} or to get the
 * instance.
 *
 * @author fredsuvn
 * @see GekBeanInfo
 */
@Immutable
@ThreadSafe
public interface GekMethodInfo extends GekMemberInfo, GekMethodBase {

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link GekMethodInfo}.
     * This method compares result of {@link Object#getClass()} and {@link GekMethodInfo#getMethod()}.
     * The code is similar to the following:
     * <pre>
     *     return Objects.equals(info.getMethod(), other.getMethod())
     *         && Objects.equals(info.getName(), other.getName());
     * </pre>
     * And it works in conjunction with {@link #hashCode(GekMethodInfo)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    static boolean equals(GekMethodInfo info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        GekMethodInfo other = (GekMethodInfo) o;
        return Objects.equals(info.getMethod(), other.getMethod())
            && Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link GekMethodInfo}.
     * This method uses {@link Object#hashCode()} of {@link GekMethodInfo#getMethod()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return info.getMethod().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(GekMethodInfo, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    static int hashCode(GekMethodInfo info) {
        return info.getMethod().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link GekMethodInfo}.
     * The code is similar to the following:
     * <pre>
     *     return info.getOwner().getType().getTypeName() + "." + info.getName()
     *         + "(" + Arrays.stream(info.getMethod().getGenericParameterTypes())
     *         .map(Type::getTypeName).collect(Collectors.joining(", ")) + ")["
     *         + info.getMethod().getGenericReturnType().getTypeName() + "]";
     * </pre>
     *
     * @param info info to be string description
     * @return a string description for given info
     */
    static String toString(GekMethodInfo info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "(" + Arrays.stream(info.getMethod().getGenericParameterTypes())
            .map(Type::getTypeName).collect(Collectors.joining(", ")) + ")["
            + info.getMethod().getGenericReturnType().getTypeName() + "]";
    }
}
