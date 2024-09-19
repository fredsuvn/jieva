package test;

import org.testng.annotations.Test;
import xyz.fslabo.test.JieTest;
import xyz.fslabo.test.JieTestException;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class TestForTest {

    @Test
    public void testExpect() throws Exception {
        Method throwError = T.class.getDeclaredMethod("throwError");
        assertEquals(JieTest.testThrow(JieTestException.class, throwError, null).getClass(), JieTestException.class);
    }

    @Test
    public void testException() {
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException("");
        });
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException(new RuntimeException());
        });
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException("", new RuntimeException());
        });
    }

    private static final class T {

        private static void throwError() {
            throw new JieTestException();
        }
    }
}
