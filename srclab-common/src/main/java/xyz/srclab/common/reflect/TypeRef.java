package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Immutable
public class TypeRef<T> {

    public static <T> TypeRef<T> of(Type type) {
        return new TypeRef<>(type);
    }

    private final Type type;

    protected TypeRef() {
        this.type = getGenericSuperclass();
    }

    private TypeRef(Type type) {
        this.type = type;
    }

    protected Type getGenericSuperclass() {
        @Nullable Type generic = TypeKit.getGenericSuperclass(getClass(), TypeRef.class);
        if (!(generic instanceof ParameterizedType)) {
            throw new IllegalStateException("Generic super class must be a parameterized type");
        }
        ParameterizedType parameterizedType = (ParameterizedType) generic;
        return parameterizedType.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
