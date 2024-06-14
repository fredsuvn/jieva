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
    static boolean equals(BeanInfo beanInfo, @Nullable Object o) {
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
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link BeanProperty}.
     * This method compares result of {@link Object#getClass()}, {@link BeanProperty#getName()} and
     * {@link BeanProperty#getOwner()}. The code is similar to the following:
     * <pre>
     *     return Objects.equals(info.getClass(), other.getClass())
     *         && Objects.equals(info.getName(), other.getName())
     *         && Objects.equals(info.getOwner(), other.getOwner());
     * </pre>
     * And it works in conjunction with {@link #hashCode(BeanProperty)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(BeanProperty info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        BeanProperty other = (BeanProperty) o;
        return Objects.equals(info.getName(), other.getName())
            && Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link BeanMethod}.
     * This method compares result of {@link Object#getClass()} and {@link BeanMethod#getMethod()}.
     * The code is similar to the following:
     * <pre>
     *     return Objects.equals(info.getMethod(), other.getMethod())
     *         && Objects.equals(info.getName(), other.getName());
     * </pre>
     * And it works in conjunction with {@link #hashCode(BeanMethod)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(BeanMethod info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        BeanMethod other = (BeanMethod) o;
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
    static int hashCode(BeanInfo beanInfo) {
        return beanInfo.getType().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link BeanProperty}.
     * This method uses {@link Object#hashCode()} of {@link BeanProperty#getName()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return info.getName().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(BeanProperty, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    public static int hashCode(BeanProperty info) {
        return info.getName().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link BeanMethod}.
     * This method uses {@link Object#hashCode()} of {@link BeanMethod#getMethod()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return info.getMethod().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(BeanMethod, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    public static int hashCode(BeanMethod info) {
        return info.getMethod().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link BeanProperty}.
     * The code is similar to the following:
     * <pre>
     *     return info.getOwner().getType().getTypeName() + "." + info.getName()
     *         + "[" + info.getType().getTypeName() + "]";
     * </pre>
     *
     * @param info info to be string description
     * @return a string description for given info
     */
    public static String toString(BeanProperty info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "[" + info.getType().getTypeName() + "]";
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link BeanMethod}.
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
    public static String toString(BeanMethod info) {
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
    static String toString(BeanInfo beanInfo) {
        return beanInfo.getType().getTypeName();
    }
}
