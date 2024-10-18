package xyz.fslabo.common.reflect;

import xyz.fslabo.common.base.ShouldNotHappenException;

import java.lang.reflect.Type;

/**
 * Exception indicates the specified type is not a primitive type.
 *
 * @author fredsuvn
 */
public class NotPrimitiveException extends ShouldNotHappenException {

    /**
     * Constructs with specified type.
     *
     * @param type specified type
     */
    public NotPrimitiveException(Type type) {
        super("Not a primitive type: " + type.getTypeName() + ".");
    }
}
