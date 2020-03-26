package xyz.srclab.common.lang;

import xyz.srclab.common.reflect.ReflectHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeRef<T> {

    public static TypeRef<?> with(Type type) {
        return new TypeRef<>(type);
    }

    private final Type type;

    protected TypeRef() {
        this.type = reflectTypeSelf();
    }

    private TypeRef(Type type) {
        this.type = type;
    }

    protected Type reflectTypeSelf() {
        Type generic = ReflectHelper.findGenericSuperclass(getClass(), TypeRef.class);
        if (!(generic instanceof ParameterizedType)) {
            throw new IllegalStateException("Must be extend from TypeRef with parameterized.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) generic;
        return parameterizedType.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
