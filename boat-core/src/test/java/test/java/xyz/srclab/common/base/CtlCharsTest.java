package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.BCtlChars;
import xyz.srclab.common.base.BEscChars;
import xyz.srclab.common.base.BSgrChars;
import xyz.srclab.common.base.BSgrParam;
import xyz.srclab.common.logging.Logs;

public class CtlCharsTest {

    @Test
    public void testShell() {
        Logs.info("Hello, world!");
        Logs.info("123{}456{}{}", BEscChars.LINEFEED, BEscChars.NEWLINE, BEscChars.RESET);
        Logs.info("{}{}{}",
            BSgrChars.foregroundRed("red"),
            BSgrChars.backgroundCyan(" "),
            BSgrChars.foregroundGreen("green")
        );
        Logs.info("{}{}{}",
            BSgrChars.withParam("bright red", BSgrParam.FOREGROUND_BRIGHT_RED),
            BSgrChars.backgroundCyan(" "),
            BSgrChars.withParam("bright green", BSgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        Logs.info("{}{}{}",
            BSgrChars.withParam("color 8", BSgrParam.foregroundColor(8)),
            BSgrChars.backgroundCyan(" "),
            BSgrChars.withParam("rgb(100, 100, 50)", BSgrParam.foregroundColor(100, 100, 50))
        );
        Logs.info(BCtlChars.BEEP);
        Logs.info("123\010456\007");
        Logs.info("123{}456{}", BCtlChars.BACKSPACES, BCtlChars.BEEP);
    }
}
