package xyz.fsgek.common.base;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * This class is a type of wrapper to hold an object and its info -- such as type. It is immutable.
 *
 * @author fredsuvn
 */
@Immutable
public interface GekObject {

    /**
     * Returns an instance of {@link GekObject} holds specified object, its type will be from {@link Object#getClass()}
     * of specified object, or {@code Object.class} if specified object is null.
     *
     * @param obj specified object
     * @return an instance of {@link GekObject} holds specified object
     */
    static GekObject of(@Nullable Object obj) {
        return of(obj, obj == null ? Object.class : obj.getClass());
    }

    /**
     * Returns an instance of {@link GekObject} holds specified object and type.
     *
     * @param obj  specified object
     * @param type specified type
     * @return an instance of {@link GekObject} holds specified object and type
     */
    static GekObject of(@Nullable Object obj, Type type) {
        final class Impl implements GekObject {

            @Override
            public @Nullable Object get() {
                return obj;
            }

            @Override
            public Type type() {
                return type;
            }
        }
        return new Impl();
    }

    /**
     * Returns empty instance of {@link GekObject} holds null, and its type is {@code Object.class}.
     *
     * @return empty instance of {@link GekObject} holds null
     */
    static GekObject empty() {
        return Companion.NULL;
    }

    /**
     * Returns held object.
     *
     * @return held object
     */
    @Nullable
    Object get();

    /**
     * Returns type of held object.
     *
     * @return type of held object
     */
    Type type();

    /**
     * Companion object of {@link GekObject}.
     */
    class Companion {
        private static final GekObject NULL = GekObject.of(null);
    }
}
