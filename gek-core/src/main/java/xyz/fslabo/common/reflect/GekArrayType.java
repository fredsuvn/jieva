package xyz.fslabo.common.reflect;

import xyz.fslabo.common.base.Gek;

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
     * This method uses {@link GekReflect#getRawType(Type)} to get raw type,
     * if the result is null, return {@code Object.class}.
     *
     * @return raw type of {@link #getGenericComponentType()}
     */
    default Class<?> getComponentType() {
        Type componentType = getGenericComponentType();
        return Gek.notNull(GekReflect.getRawType(componentType), Object.class);
    }

    /**
     * Returns array type of which component type is from {@link #getComponentType()}.
     * Default implementation uses {@link GekReflect#arrayClass(Type)}:
     * <pre>
     *     return GekReflect.arrayClass(getComponentType());
     * </pre>
     *
     * @return array type of which component type is from {@link #getComponentType()}
     */
    default Class<?> getArrayType() {
        return GekReflect.arrayClass(getComponentType());
    }
}
