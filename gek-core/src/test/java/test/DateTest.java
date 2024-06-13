package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.GekDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class DateTest {

    @Test
    public void testDate() throws ParseException {
        Date now = new Date();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now);
        Assert.assertEquals(
            GekDate.toString(now, "yyyy-MM-dd HH:mm:ss.SSS"),
            dateStr
        );
        Assert.assertEquals(
            GekDate.toDate(dateStr, "yyyy-MM-dd HH:mm:ss.SSS"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(dateStr)
        );
        Assert.assertEquals(
            GekDate.toInstant(GekDate.FORMATTER_OFFSET.parse("2066-06-06 06:06:06.000 +0800")),
            Instant.parse("2066-06-05T22:06:06Z")
        );
        TemporalAccessor temporal = GekDate.toFormatter("yyyy-MM-dd'T'HH:mm:ssX").parse("2066-06-05T22:06:06Z");
        Assert.assertEquals(
            Instant.from(temporal),
            Instant.parse("2066-06-05T22:06:06Z")
        );
        Assert.assertEquals(
            GekDate.toDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse("2066-06-05T22:06:06Z").toInstant(),
            Instant.parse("2066-06-05T22:06:06Z")
        );
    }

    @Test
    public void testPattern() {
        //Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2066-06-06 06:06:06.666");
        Assert.assertEquals(
            GekDate.toPattern("20111203"),
            "yyyyMMdd"
        );
        Assert.assertEquals(
            GekDate.toPattern("10:15:30"),
            "HH:mm:ss"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03"),
            "yyyy-MM-dd"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03+01:00"),
            "yyyy-MM-ddZZZZZ"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03 +0100"),
            "yyyy-MM-dd ZZZ"
        );
        Assert.assertEquals(
            GekDate.toPattern("10:15"),
            "HH:mm"
        );
        Assert.assertEquals(
            GekDate.toPattern("10:15:30.500"),
            "HH:mm:ss.SSS"
        );
        Assert.assertEquals(
            GekDate.toPattern("10:15:30+01:00"),
            "HH:mm:ssZZZZZ"
        );
        Assert.assertEquals(
            GekDate.toPattern("10:15:30 +0100"),
            "HH:mm:ss ZZZ"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03T10:15:30"),
            "yyyy-MM-dd'T'HH:mm:ss"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03 10:15:30"),
            "yyyy-MM-dd HH:mm:ss"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03T10:15:30+01:00"),
            "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03T10:15:30 +0100"),
            "yyyy-MM-dd'T'HH:mm:ss ZZZ"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03 10:15:30 +0100"),
            "yyyy-MM-dd HH:mm:ss ZZZ"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03T10:15:30Z"),
            "yyyy-MM-dd'T'HH:mm:ssX"
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03 10:15:30.500"),
            GekDate.PATTERN
        );
        Assert.assertEquals(
            GekDate.toPattern("2011-12-03 10:15:30.500 +0100"),
            GekDate.PATTERN_OFFSET
        );
    }

    @Test
    public void testFormatter() {
        Assert.assertSame(
            GekDate.toFormatter(GekDate.PATTERN),
            GekDate.FORMATTER
        );
        Assert.assertSame(
            GekDate.toFormatter(GekDate.PATTERN_OFFSET),
            GekDate.FORMATTER_OFFSET
        );
        DateTimeFormatter formatter = GekDate.toFormatter("yyyyMMddHHmm");
        Assert.assertSame(
            GekDate.toFormatter("yyyyMMddHHmm"),
            formatter
        );
    }
}
