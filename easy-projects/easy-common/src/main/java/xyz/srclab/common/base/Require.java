package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.util.function.Supplier;

/**
 * @author sunqian
 */
public class Require {

    public static <T> T nonNullElement(@Nullable T nullable) {
        Checker.checkElement(nullable != null);
        return nullable;
    }

    public static <T> T nonNullElement(@Nullable T nullable, String message) {
        Checker.checkElement(nullable != null, message);
        return nullable;
    }

    public static <T> T nonNullElement(@Nullable T nullable, Supplier<String> messageSupplier) {
        Checker.checkElement(nullable != null, messageSupplier);
        return nullable;
    }
}
