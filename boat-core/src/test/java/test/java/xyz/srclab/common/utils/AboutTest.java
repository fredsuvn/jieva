package test.java.xyz.srclab.common.utils;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.utils.About;
import xyz.srclab.common.utils.Author;
import xyz.srclab.common.utils.SemVer;

import java.util.Collections;

public class AboutTest {

    @Test
    public void testVersion() {
        String verString1 = "1.2.3-beta.2.3+123";
        String verString2 = "1.2.3-alpha.2.3+123";
        String verString3 = "1.2.3+123";
        String verString4 = "1.2.3+erfsafsafs";
        String verString5 = "1.2.4";
        SemVer v1 = SemVer.parse(verString1);
        SemVer v2 = SemVer.parse(verString2);
        SemVer v3 = SemVer.parse(verString3);
        SemVer v4 = SemVer.parse(verString4);
        SemVer v5 = SemVer.parse(verString5);
        BLog.info("SemVer1: {}", v1);
        BLog.info("SemVer2: {}", v2);
        BLog.info("SemVer3: {}", v3);
        BLog.info("SemVer4: {}", v4);
        BLog.info("SemVer5: {}", v5);
        Assert.assertTrue(v1.compareTo(v2) > 0);
        Assert.assertTrue(v3.compareTo(v1) > 0);
        Assert.assertTrue(v3.compareTo(v2) > 0);
        Assert.assertEquals(v3.compareTo(v4), 0);
        Assert.assertTrue(v3.compareTo(v5) < 0);
    }

    @Test
    public void testAbout() {
        About about = About.of(
            "name",
            "1.2.3",
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
        BLog.info("about: {}", about);
    }
}
