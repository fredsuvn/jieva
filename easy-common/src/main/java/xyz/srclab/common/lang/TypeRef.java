package xyz.srclab.common.lang;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Immutable
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
        @Nullable Type generic = TypeHelper.findGenericSuperclass(getClass(), TypeRef.class);
        if (!(generic instanceof ParameterizedType)) {
            throw new IllegalStateException("Must be extend from TypeRef with parameterized.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) generic;
        return parameterizedType.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    public <S> Class<S> getRawType() {
        return TypeHelper.getRawClass(type);
    }
}
