package xyz.fsgek.common.base;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Gek Object contains a value and its type to describe an object.
 * The value is typically the object itself.
 * <p>
 * it is immutable.
 *
 * @author fredsuvn
 */
@Immutable
public interface GekObject {

    /**
     * Returns an instance of {@link GekObject} of specified value, and its type will be set to
     * {@link Object#getClass()} of the value, or {@code Object.class} if the value is null.
     *
     * @param value specified value
     * @return an instance of {@link GekObject} which contains specified object
     */
    static GekObject of(@Nullable Object value) {
        return of(value, value == null ? Object.class : value.getClass());
    }

    /**
     * Returns an instance of {@link GekObject} of specified value and type.
     *
     * @param value specified value
     * @param type  specified type
     * @return an instance of {@link GekObject} of specified value and type
     */
    static GekObject of(@Nullable Object value, Type type) {
        return Impls.newGekObject(value, type);
    }

    /**
     * Returns an instance of {@link GekObject} of null value and type of {@code Object.class}.
     *
     * @return an instance of {@link GekObject} of null value and type of {@code Object.class}
     */
    static GekObject empty() {
        return Impls.nullGekObject();
    }

    /**
     * Returns value of object.
     *
     * @return value of object
     */
    @Nullable
    Object getValue();

    /**
     * Returns type of object.
     *
     * @return type of object
     */
    Type getType();
}
