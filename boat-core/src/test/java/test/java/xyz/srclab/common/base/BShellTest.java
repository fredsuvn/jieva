package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BShellTest {

    public static void main(String[] args) {
        Shell shell = Shell.systemDefault();
        while (true) {
            String input = shell.next();
            shell.println("You input: " + input);
            if ("q".equals(input)) {
                break;
            }
        }
    }

    @Test
    public void testShell() {
        String message = BRandom.randomString(100);
        InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        StringBuilder output = new StringBuilder();
        Shell shell = Shell.of(inputStream, output);
        String next = shell.next();
        BLog.info("shell.next: {}", next);
        Assert.assertEquals(next, message);
        shell.println(message);
        Assert.assertEquals(output.toString(), message + BSystem.lineSeparator());
    }

    @Test
    public void testChars() {
        BLog.info("Hello, world!");
        BLog.info("123{}456{}{}", EscChars.linefeed(), EscChars.newline(), EscChars.reset());
        BLog.info("{}{}{}",
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        );
        BLog.info("{}{}{}",
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        BLog.info("{}{}{}",
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        );
        BLog.info(CtlChars.beep());
        BLog.info("123\010456\007");
        BLog.info("123{}456{}", CtlChars.backspaces(), CtlChars.beep());
    }
}
