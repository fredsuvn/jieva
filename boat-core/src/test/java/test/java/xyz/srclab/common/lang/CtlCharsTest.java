package test.java.xyz.srclab.common.lang;

import org.testng.annotations.Test;
import xyz.srclab.common.lang.CtlChars;
import xyz.srclab.common.lang.EscChars;
import xyz.srclab.common.lang.SgrChars;
import xyz.srclab.common.base.SgrParam;
import xyz.srclab.common.test.TestLogger;

public class CtlCharsTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testShell() {
        logger.log("Hello, world!");
        logger.log("123{}456{}{}", EscChars.linefeed(), EscChars.newline(), EscChars.reset());
        logger.log("{}{}{}",
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        );
        logger.log("{}{}{}",
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        logger.log("{}{}{}",
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        );
        logger.log(CtlChars.beep());
        logger.log("123\010456\007");
        logger.log("123{}456{}", CtlChars.backspaces(), CtlChars.beep());
    }
}
