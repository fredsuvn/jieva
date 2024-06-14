package xyz.fslabo.common.reflect;

import xyz.fslabo.common.base.Jie;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * This interface is sub and enhanced type of {@link GenericArrayType}.
 *
 * @author fredsuvn
 */
public interface GekArrayType extends GenericArrayType {

    /**
     * Returns raw type of {@link #getGenericComponentType()}.
     * This method uses {@link JieReflect#getRawType(Type)} to get raw type,
     * if the result is null, return {@code Object.class}.
     *
     * @return raw type of {@link #getGenericComponentType()}
     */
    default Class<?> getComponentType() {
        Type componentType = getGenericComponentType();
        return Jie.orDefault(JieReflect.getRawType(componentType), Object.class);
    }

    /**
     * Returns array type of which component type is from {@link #getComponentType()}.
     * Default implementation uses {@link JieReflect#arrayClass(Type)}:
     * <pre>
     *     return JieReflect.arrayClass(getComponentType());
     * </pre>
     *
     * @return array type of which component type is from {@link #getComponentType()}
     */
    default Class<?> getArrayType() {
        return JieReflect.arrayClass(getComponentType());
    }
}
