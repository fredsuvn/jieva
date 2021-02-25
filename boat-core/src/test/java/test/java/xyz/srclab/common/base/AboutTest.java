package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.About;
import xyz.srclab.common.base.Licence;
import xyz.srclab.common.base.PoweredBy;
import xyz.srclab.common.base.SemVer;
import xyz.srclab.common.test.TestLogger;

public class AboutTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

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
        logger.log(v1);
        logger.log(v2);
        logger.log(v3);
        logger.log(v4);
        logger.log(v5);
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
                "url",
                SemVer.parse("1.0.0"),
                Licence.of("licence", "url"),
                PoweredBy.of("poweredBy", "url", "mail")
        );
        logger.log(about);
    }
}
