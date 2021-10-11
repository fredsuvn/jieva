package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Dates;
import xyz.srclab.common.logging.Logs;

import java.time.*;
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
        Assert.assertEquals(DateTimeFormatter.ofPattern(Dates.TIMESTAMP_PATTERN).format(now), toTimestamp);
        Assert.assertEquals(Dates.toInstant(now), now.atZone(ZoneId.systemDefault()).toInstant());
        Assert.assertEquals(Dates.toDate(now), Date.from(Dates.toInstant(now)));
        Assert.assertEquals(Dates.toLocalDate(now), now.toLocalDate());
        Assert.assertEquals(Dates.toLocalTime(now), now.toLocalTime());
        Assert.assertEquals(Dates.toZonedDateTime(now), now.atZone(ZoneId.systemDefault()));
    }

    @Test
    public void testDateString() {
        String dateString1 = "2021-09-16 03:00:18";
        Assert.assertEquals(
            Dates.toLocalDateTime(dateString1),
            LocalDateTime.of(2021, 9, 16, 3, 0, 18)
        );

        String dateString2 = "2021-09-16 03:00:18 +08:00";
        Assert.assertEquals(
            Dates.toOffsetDateTime(dateString2),
            OffsetDateTime.of(2021, 9, 16, 3, 0, 18, 0, ZoneOffset.ofHours(8))
        );

        String dateString3 = "2021-09-16T03:00:18";
        Assert.assertEquals(
            Dates.toLocalDateTime(dateString3),
            LocalDateTime.of(2021, 9, 16, 3, 0, 18)
        );

        Assert.assertEquals(
            Dates.toLocalDate(dateString3),
            LocalDate.of(2021, 9, 16)
        );

        Assert.assertEquals(
            Dates.toLocalTime(dateString3),
            LocalTime.of(3, 0, 18)
        );

        String dateString4 = "2021-09-16T03:00:18+08:00";
        Assert.assertEquals(
            Dates.toOffsetDateTime(dateString4),
            OffsetDateTime.of(2021, 9, 16, 3, 0, 18, 0, ZoneOffset.ofHours(8))
        );

        String dateString5 = "2011-12-03T10:15:30+01:00[Europe/Paris]";
        Assert.assertEquals(
            Dates.toZonedDateTime(dateString5),
            ZonedDateTime.of(2011, 12, 3, 10, 15, 30, 0, ZoneId.of("Europe/Paris"))
        );
    }

    @Test
    public void testDuration() {
        Duration duration = Duration.ofSeconds(30);
        String durationString = duration.toString();
        Logs.info("durationString: {}", durationString);
        Assert.assertEquals(
            Dates.toDuration(durationString),
            duration
        );
    }
}
