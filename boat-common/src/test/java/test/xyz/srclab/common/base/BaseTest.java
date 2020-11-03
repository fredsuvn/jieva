package test.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.Format;

/**
 * @author sunqian
 */
public class BaseTest {

    @Test
    public void testFormat() {
        System.out.println(
                Format.fastFormat("This is {} {}!", "fast", "format")
        );
        System.out.println(
                Format.printfFormat("This is %s %s!", "printf", "format")
        );
        System.out.println(
                Format.messageFormat("This is {0} {1}!", "message", "format")
        );
    }
}
