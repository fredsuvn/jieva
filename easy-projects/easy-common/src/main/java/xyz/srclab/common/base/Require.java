package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
public class Require {

    public static <T> T nonNull(@Nullable T nullable) throws NullPointerException {
        return Objects.requireNonNull(nullable);
    }

    public static <T> T nonNullElement(@Nullable T nullable) throws NoSuchElementException {
        Check.checkElement(nullable != null);
        return nullable;
    }

    public static <T> T nonNullElement(@Nullable T nullable, String message) throws NoSuchElementException {
        Check.checkElement(nullable != null, message);
        return nullable;
    }

    public static <T> T nonNullElement(@Nullable T nullable, Supplier<String> messageSupplier)
            throws NoSuchElementException {
        Check.checkElement(nullable != null, messageSupplier);
        return nullable;
    }
}
