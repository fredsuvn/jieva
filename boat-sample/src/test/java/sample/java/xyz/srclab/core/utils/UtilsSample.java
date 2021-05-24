package sample.java.xyz.srclab.core.utils;

import org.testng.annotations.Test;
import xyz.srclab.common.test.TestLogger;
import xyz.srclab.common.utils.About;
import xyz.srclab.common.utils.Author;
import xyz.srclab.common.utils.Counter;
import xyz.srclab.common.utils.SemVer;

import java.util.Collections;

public class UtilsSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testCounter() {
        Counter counter = Counter.startsAt(100);
        counter.getAndIncrementInt();
        counter.reset();
        Counter atomicCounter = Counter.startsAt(100, true);
        atomicCounter.incrementAndGetInt();
        atomicCounter.reset();
    }

    @Test
    public void testAbout() {
        String verString = "1.2.3-beta.2.3+123";
        SemVer semVer = SemVer.parse(verString);
        About about = About.of(
            "name",
            semVer.normalString(),
            Collections.singletonList(Author.of("name", "author@mail.com", null)),
            "123@123.com",
            "url",
            Collections.singletonList("licence"),
            Collections.singletonList(About.of(
                "poweredBy",
                null,
                Collections.emptyList(),
                null,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                null
            )),
            "© 2021 SrcLab"
        );
        //name
        //Version: 1.2.3
        //Author: name(author@mail.com)
        //Mail: 123@123.com
        //Url: url
        //Licence: licence
        //Powered by: poweredBy
        //© 2021 SrcLab
        logger.log("About: {}", about);
    }
}
