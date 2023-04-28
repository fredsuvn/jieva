package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtDate;
import xyz.srclab.common.base.DatePattern;
import xyz.srclab.common.base.TimePoint;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author sunqian
 */
public class BtDateTest {

    @Test
    public void testTime() {
        String timestamp = BtDate.currentTimestamp();
        LocalDateTime now = LocalDateTime.from(BtDate.defaultTimestampDatePattern().formatter().parse(timestamp));
        Assert.assertEquals(DateTimeFormatter.ofPattern(BtDate.defaultTimestampDatePattern().getPattern()).format(now), timestamp);
        Assert.assertEquals(BtDate.toInstant(now), now.atZone(ZoneId.systemDefault()).toInstant());
        Assert.assertEquals(BtDate.toDate(now), Date.from(BtDate.toInstant(now)));
        Assert.assertEquals(BtDate.toLocalDate(now), now.toLocalDate());
        Assert.assertEquals(BtDate.toLocalTime(now), now.toLocalTime());
        Assert.assertEquals(BtDate.toZonedDateTime(now), now.atZone(ZoneId.systemDefault()));
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
            BtDate.toLocalDateTime(pattern.parseTemporal(date)),
            LocalDateTime.of(2021, 9, 16, 3, 0, 18)
        );
        Assert.assertEquals(
            BtDate.toZonedDateTime(pattern.parseTemporal(date)),
            ZonedDateTime.of(LocalDateTime.of(2021, 9, 16, 3, 0, 18), ZoneId.systemDefault())
        );
    }

    @Test
    public void testParsedDate() {
        String time = "20220307171330007";
        TimePoint timePoint = TimePoint.of(time);
        LocalDateTime localDateTime = LocalDateTime.of(2022, 3, 7, 17, 13, 30, 7_000_000);
        Assert.assertEquals(timePoint.toLocalDateTime(), localDateTime);
    }
}
