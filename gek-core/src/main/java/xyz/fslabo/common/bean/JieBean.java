package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilities for bean operation.
 *
 * @author fredsuvn
 */
public class JieBean {

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link BeanInfo}.
     * This method uses result of {@link BeanInfo#getType()} to compare.
     * The code is similar to the following:
     * <pre>
     *     return Objects.equals(bean.getType(), other.getType());
     * </pre>
     * And it works in conjunction with {@link #hashCode(BeanInfo)}.
     *
     * @param beanInfo comparing bean info
     * @param o        object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(BeanInfo beanInfo, @Nullable Object o) {
        if (beanInfo == o) {
            return true;
        }
        if (o == null || !beanInfo.getClass().equals(o.getClass())) {
            return false;
        }
        BeanInfo other = (BeanInfo) o;
        return Objects.equals(beanInfo.getType(), other.getType());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link PropertyInfo}.
     * This method compares result of {@link Object#getClass()}, {@link PropertyInfo#getName()} and
     * {@link PropertyInfo#getOwner()}. The code is similar to the following:
     * <pre>
     *     return Objects.equals(info.getClass(), other.getClass())
     *         && Objects.equals(info.getName(), other.getName())
     *         && Objects.equals(info.getOwner(), other.getOwner());
     * </pre>
     * And it works in conjunction with {@link #hashCode(PropertyInfo)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(PropertyInfo info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        PropertyInfo other = (PropertyInfo) o;
        return Objects.equals(info.getName(), other.getName())
            && Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link MethodInfo}.
     * This method compares result of {@link Object#getClass()} and {@link MethodInfo#getMethod()}.
     * The code is similar to the following:
     * <pre>
     *     return Objects.equals(info.getMethod(), other.getMethod())
     *         && Objects.equals(info.getName(), other.getName());
     * </pre>
     * And it works in conjunction with {@link #hashCode(MethodInfo)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(MethodInfo info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        MethodInfo other = (MethodInfo) o;
        return Objects.equals(info.getMethod(), other.getMethod())
            && Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link BeanInfo}.
     * This method uses {@link Object#hashCode()} of {@link BeanInfo#getType()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return bean.getType().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(BeanInfo, Object)}.
     *
     * @param beanInfo bean info to be hashed
     * @return hash code of given bean
     */
    public static int hashCode(BeanInfo beanInfo) {
        return beanInfo.getType().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link PropertyInfo}.
     * This method uses {@link Object#hashCode()} of {@link PropertyInfo#getName()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return info.getName().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(PropertyInfo, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    public static int hashCode(PropertyInfo info) {
        return info.getName().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link MethodInfo}.
     * This method uses {@link Object#hashCode()} of {@link MethodInfo#getMethod()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return info.getMethod().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(MethodInfo, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    public static int hashCode(MethodInfo info) {
        return info.getMethod().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link PropertyInfo}.
     * The code is similar to the following:
     * <pre>
     *     return info.getOwner().getType().getTypeName() + "." + info.getName()
     *         + "[" + info.getType().getTypeName() + "]";
     * </pre>
     *
     * @param info info to be string description
     * @return a string description for given info
     */
    public static String toString(PropertyInfo info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "[" + info.getType().getTypeName() + "]";
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link MethodInfo}.
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
    public static String toString(MethodInfo info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "(" + Arrays.stream(info.getMethod().getGenericParameterTypes())
            .map(Type::getTypeName).collect(Collectors.joining(", ")) + ")["
            + info.getMethod().getGenericReturnType().getTypeName() + "]";
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link BeanInfo}.
     * The code is similar to the following:
     * <pre>
     *     return beanInfo.getType().getTypeName();
     * </pre>
     *
     * @param beanInfo bean info to be string description
     * @return a string description for given descriptor
     */
    public static String toString(BeanInfo beanInfo) {
        return beanInfo.getType().getTypeName();
    }
}
