package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.About;
import xyz.srclab.common.base.Licence;
import xyz.srclab.common.base.PoweredBy;
import xyz.srclab.common.base.Version;

public class AboutTest {

    @Test
    public void testVersion() {
        String verString1 = "1.2.3-beta.2.3+123";
        String verString2 = "1.2.3-alpha.2.3+123";
        String verString3 = "1.2.3+123";
        String verString4 = "1.2.3+erfsafsafs";
        String verString5 = "1.2.4";
        Version v1 = Version.parse(verString1);
        Version v2 = Version.parse(verString2);
        Version v3 = Version.parse(verString3);
        Version v4 = Version.parse(verString4);
        Version v5 = Version.parse(verString5);
        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);
        System.out.println(v4);
        System.out.println(v5);
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
                Version.parse("1.0.0"),
                Licence.of("licence", "url"),
                PoweredBy.of("poweredBy", "url", "mail")
        );
        System.out.println(about);
    }
}
