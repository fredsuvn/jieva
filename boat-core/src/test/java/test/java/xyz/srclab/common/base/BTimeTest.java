package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BTime;
import xyz.srclab.common.base.DatePattern;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author sunqian
 */
public class BTimeTest {

    @Test
    public void testTime() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = BTime.timestamp();
        Assert.assertEquals(DateTimeFormatter.ofPattern(BTime.TIMESTAMP_PATTERN.getPattern()).format(now), timestamp);
        Assert.assertEquals(BTime.toInstant(now), now.atZone(ZoneId.systemDefault()).toInstant());
        Assert.assertEquals(BTime.toDate(now), Date.from(BTime.toInstant(now)));
        Assert.assertEquals(BTime.toLocalDate(now), now.toLocalDate());
        Assert.assertEquals(BTime.toLocalTime(now), now.toLocalTime());
        Assert.assertEquals(BTime.toZonedDateTime(now), now.atZone(ZoneId.systemDefault()));
    }

    @Test
    public void testDatePattern() {
        DatePattern datePattern1 = DatePattern.of("yyyy-MM-dd hh:mm:ss");
        String dateString1 = "2021-09-16 03:00:18";
        Assert.assertEquals(
            datePattern1.parseLocalDateTime(dateString1),
            LocalDateTime.of(2021, 9, 16, 3, 0, 18)
        );
        Assert.assertEquals(
            datePattern1.buildLocalDateTime(dateString1),
            LocalDateTime.of(2021, 9, 16, 3, 0, 18)
        );
        Assert.assertEquals(
            datePattern1.buildLocalDateTimeOrNull(dateString1),
            LocalDateTime.of(2021, 9, 16, 3, 0, 18)
        );
    }
}
