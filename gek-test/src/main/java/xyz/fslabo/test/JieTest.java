package xyz.fslabo.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.testng.Assert.expectThrows;

/**
 * Test utilities.
 *
 * @author fredsuvn
 */
public class JieTest {

    /**
     * Tests specified method, expect to throw an exception.
     *
     * @param exception the exception to be expected
     * @param method    specified method
     * @param inst      instance for the method invoked
     * @param args      arguments of invoking
     * @param <T>       type of exception to be expected
     */
    public static <T extends Throwable> T testThrow(
        Class<T> exception, Method method, Object inst, Object... args) {
        method.setAccessible(true);
        return expectThrows(exception, () -> {
            try {
                method.invoke(inst, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }
}
