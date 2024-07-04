package xyz.fslabo.common.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * This interface is sub and enhanced type of {@link GenericArrayType}.
 *
 * @author fredsuvn
 */
public interface ArrayType extends GenericArrayType {

    /**
     * Returns raw array type, for example: List&lt;String&gt;[] -> List[].
     *
     * @return raw array type
     */
    default Class<?> getRawArrayType() {
        Type componentType = getGenericComponentType();
        Class<?> componentClass = JieReflect.getRawType(componentType);
        if (componentClass == null) {
            throw new UnsupportedOperationException("Unsupported component type: " + componentType);
        }
        return JieReflect.arrayClass(componentClass);
    }
}
