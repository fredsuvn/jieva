package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BTime;
import xyz.srclab.common.base.DatePattern;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author sunqian
 */
public class BTimeTest {

    @Test
    public void testTime() {
        String timestamp = BTime.timestamp();
        LocalDateTime now = LocalDateTime.from(BTime.TIMESTAMP_PATTERN.toFormatter().parse(timestamp));
        Assert.assertEquals(DateTimeFormatter.ofPattern(BTime.TIMESTAMP_PATTERN.getPattern()).format(now), timestamp);
        Assert.assertEquals(BTime.toInstant(now), now.atZone(ZoneId.systemDefault()).toInstant());
        Assert.assertEquals(BTime.toDate(now), Date.from(BTime.toInstant(now)));
        Assert.assertEquals(BTime.toLocalDate(now), now.toLocalDate());
        Assert.assertEquals(BTime.toLocalTime(now), now.toLocalTime());
        Assert.assertEquals(BTime.toZonedDateTime(now), now.atZone(ZoneId.systemDefault()));
    }

    @Test
    public void testParseDate() {
        DatePattern pattern = DatePattern.of("yyyy-MM-dd HH:mm:ss.SSS");
        String date = "2021-09-16 03:00:18.000";
        Assert.assertEquals(
            pattern.parseLocalDateTime(date),
            LocalDateTime.of(2021, 9, 16, 3, 0, 18)
        );
    }

    @Test
    public void testBuildDate() {
        DatePattern pattern = DatePattern.of("yyyy-MM-dd hh:mm:ss");
        String date = "2021-09-16 03:00:18";
        Assert.assertEquals(
            BTime.toLocalDateTime(pattern.parseTemporalAccessor(date)),
            LocalDateTime.of(2021, 9, 16, 3, 0, 18)
        );
        Assert.assertEquals(
            BTime.toZonedDateTime(pattern.parseTemporalAccessor(date)),
            ZonedDateTime.of(LocalDateTime.of(2021, 9, 16, 3, 0, 18), ZoneId.systemDefault())
        );
    }
}
