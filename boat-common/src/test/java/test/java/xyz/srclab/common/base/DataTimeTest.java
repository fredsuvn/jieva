package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author sunqian
 */
public class DataTimeTest {

    @Test
    public void testDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String toTimestamp = DateTime.toTimestamp(now);
        Assert.assertEquals(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(now), toTimestamp);
        Assert.assertEquals(DateTime.toInstant(now), now.atZone(ZoneId.systemDefault()).toInstant());
    }
}
