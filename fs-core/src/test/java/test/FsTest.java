package test;

import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;

public class FsTest {

    @Test
    public void testThrow() {
        Out.println(Fs.stackTraceToString(
            new IllegalArgumentException(new IllegalStateException(new NullPointerException())))
        );
        Out.println(Fs.stackTraceToString(
            new IllegalArgumentException(new IllegalStateException(new NullPointerException())),
            " : ")
        );
    }
}
