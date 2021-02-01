package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Dates;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author sunqian
 */
public class DatesTest {

    @Test
    public void testDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String toTimestamp = Dates.toTimestamp(now);
        Assert.assertEquals(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(now), toTimestamp);
        Assert.assertEquals(Dates.toInstant(now), now.atZone(ZoneId.systemDefault()).toInstant());
    }
}
