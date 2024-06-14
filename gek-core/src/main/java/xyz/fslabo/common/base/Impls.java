package xyz.fslabo.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Type;

final class Impls {

    static GekObject nullGekObject() {
        return GekObjectImpl.NULL;
    }

    static GekObject newGekObject(@Nullable Object value, Type type) {
        return new GekObjectImpl(value, type);
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    private static final class GekObjectImpl implements GekObject {

        private static final GekObject NULL = new GekObjectImpl(null, Object.class);

        private final Object value;
        private final Type type;
    }
}
