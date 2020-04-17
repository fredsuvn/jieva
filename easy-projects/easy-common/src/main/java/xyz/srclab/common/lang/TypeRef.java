package xyz.srclab.common.lang;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Immutable
public class TypeRef<T extends Type> {

    public static <T extends Type> TypeRef<T> with(T type) {
        return new TypeRef<>(type);
    }

    private final T type;

    protected TypeRef() {
        this.type = (T) reflectTypeSelf();
    }

    private TypeRef(T type) {
        this.type = type;
    }

    protected Type reflectTypeSelf() {
        @Nullable Type generic = TypeHelper.findSuperclassGeneric(getClass(), TypeRef.class);
        if (!(generic instanceof ParameterizedType)) {
            throw new IllegalStateException("Must be extend from parameterized type.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) generic;
        return parameterizedType.getActualTypeArguments()[0];
    }

    public T getType() {
        return type;
    }

    public <U> Class<U> getRawType() {
        return (Class<U>) TypeHelper.getRawClass(type);
    }
}
