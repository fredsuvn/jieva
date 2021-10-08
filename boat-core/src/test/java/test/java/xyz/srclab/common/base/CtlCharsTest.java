package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.CtlChars;
import xyz.srclab.common.base.EscChars;
import xyz.srclab.common.base.SgrChars;
import xyz.srclab.common.base.SgrParam;
import xyz.srclab.common.logging.Logger;

public class CtlCharsTest {

    private static final Logger logger = Logger.simpleLogger();

    @Test
    public void testShell() {
        logger.info("Hello, world!");
        logger.info("123{}456{}{}", EscChars.LINEFEED, EscChars.NEWLINE, EscChars.RESET);
        logger.info("{}{}{}",
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        );
        logger.info("{}{}{}",
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        logger.info("{}{}{}",
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        );
        logger.info(CtlChars.BEEP);
        logger.info("123\010456\007");
        logger.info("123{}456{}", CtlChars.BACKSPACES, CtlChars.BEEP);
    }
}
