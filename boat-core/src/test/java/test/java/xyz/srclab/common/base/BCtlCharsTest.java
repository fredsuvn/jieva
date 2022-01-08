package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.*;

public class BCtlCharsTest {

    @Test
    public void testShell() {
        BLog.info("Hello, world!");
        BLog.info("123{}456{}{}", BEscChars.LINEFEED, BEscChars.NEWLINE, BEscChars.RESET);
        BLog.info("{}{}{}",
            BSgrChars.foregroundRed("red"),
            BSgrChars.backgroundCyan(" "),
            BSgrChars.foregroundGreen("green")
        );
        BLog.info("{}{}{}",
            BSgrChars.withParam("bright red", BSgrParam.FOREGROUND_BRIGHT_RED),
            BSgrChars.backgroundCyan(" "),
            BSgrChars.withParam("bright green", BSgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        BLog.info("{}{}{}",
            BSgrChars.withParam("color 8", BSgrParam.foregroundColor(8)),
            BSgrChars.backgroundCyan(" "),
            BSgrChars.withParam("rgb(100, 100, 50)", BSgrParam.foregroundColor(100, 100, 50))
        );
        BLog.info(BCtlChars.BEEP);
        BLog.info("123\010456\007");
        BLog.info("123{}456{}", BCtlChars.BACKSPACES, BCtlChars.BEEP);
    }
}
