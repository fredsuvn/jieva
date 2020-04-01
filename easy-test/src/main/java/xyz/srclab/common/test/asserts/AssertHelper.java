package xyz.srclab.common.test.asserts;

import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import xyz.srclab.common.string.tostring.ToString;

public class AssertHelper {

    public static void printAssert(@Nullable Object actual, @Nullable Object expected) {
        System.out.println(
                "Assert >>> actual: " +
                        ToString.toString(actual) +
                        "; expected: " +
                        ToString.toString(expected)
        );
        Assert.assertEquals(actual, expected);
    }

    public static void printAssertThrows(Class<? extends Throwable> expected, Runnable actual) {
        Assert.assertThrows(expected, () -> {
            System.out.println("Assert throws >>> expected: " + ToString.toString(expected));
            actual.run();
        });
    }
}
