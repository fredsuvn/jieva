package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.CtlChars;
import xyz.srclab.common.base.EscChars;
import xyz.srclab.common.base.SgrChars;
import xyz.srclab.common.base.SgrParam;
import xyz.srclab.common.logging.Logs;

public class CtlCharsTest {

    @Test
    public void testShell() {
        Logs.info("Hello, world!");
        Logs.info("123{}456{}{}", EscChars.LINEFEED, EscChars.NEWLINE, EscChars.RESET);
        Logs.info("{}{}{}",
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        );
        Logs.info("{}{}{}",
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        Logs.info("{}{}{}",
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        );
        Logs.info(CtlChars.BEEP);
        Logs.info("123\010456\007");
        Logs.info("123{}456{}", CtlChars.BACKSPACES, CtlChars.BEEP);
    }
}
