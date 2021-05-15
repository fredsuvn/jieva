package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Dates;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
        Assert.assertEquals(Dates.toDate(now), Date.from(Dates.toInstant(now)));
        Assert.assertEquals(Dates.toLocalDate(now), now.toLocalDate());
        Assert.assertEquals(Dates.toLocalTime(now), now.toLocalTime());
        Assert.assertEquals(Dates.toZonedDateTime(now), now.atZone(ZoneId.systemDefault()));
    }
}
