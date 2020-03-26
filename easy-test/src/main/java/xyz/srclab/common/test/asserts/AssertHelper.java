package xyz.srclab.common.test.asserts;

import org.jetbrains.annotations.Nullable;
import org.testng.Assert;

public class AssertHelper {

    public static void printAssert(@Nullable Object actual, @Nullable Object expected) {
        System.out.println("Assert >>> actual: " + actual + "; expected: " + expected);
        Assert.assertEquals(actual, expected);
    }

    public static void printAssertThrows(Class<? extends Throwable> expected, Runnable actual) {
        Assert.assertThrows(expected, () -> {
            System.out.println("Assert throws >>> expected: " + expected);
            actual.run();
        });
    }
}
