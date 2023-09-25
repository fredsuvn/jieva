package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsDate;

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
            FsDate.toString(now, "yyyy-MM-dd HH:mm:ss.SSS"),
            dateStr
        );
        Assert.assertEquals(
            FsDate.toDate(dateStr, "yyyy-MM-dd HH:mm:ss.SSS"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(dateStr)
        );
        Assert.assertEquals(
            FsDate.toInstant(FsDate.FORMATTER_OFFSET.parse("2066-06-06 06:06:06.000 +0800")),
            Instant.parse("2066-06-05T22:06:06Z")
        );
        TemporalAccessor temporal = FsDate.toFormatter("yyyy-MM-dd'T'HH:mm:ssX").parse("2066-06-05T22:06:06Z");
        Assert.assertEquals(
            Instant.from(temporal),
            Instant.parse("2066-06-05T22:06:06Z")
        );
        Assert.assertEquals(
            FsDate.toDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse("2066-06-05T22:06:06Z").toInstant(),
            Instant.parse("2066-06-05T22:06:06Z")
        );
    }

    @Test
    public void testPattern() {
        //Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2066-06-06 06:06:06.666");
        Assert.assertEquals(
            FsDate.toPattern("20111203"),
            "yyyyMMdd"
        );
        Assert.assertEquals(
            FsDate.toPattern("10:15:30"),
            "HH:mm:ss"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03"),
            "yyyy-MM-dd"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03+01:00"),
            "yyyy-MM-ddZZZZZ"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03 +0100"),
            "yyyy-MM-dd ZZZ"
        );
        Assert.assertEquals(
            FsDate.toPattern("10:15"),
            "HH:mm"
        );
        Assert.assertEquals(
            FsDate.toPattern("10:15:30.500"),
            "HH:mm:ss.SSS"
        );
        Assert.assertEquals(
            FsDate.toPattern("10:15:30+01:00"),
            "HH:mm:ssZZZZZ"
        );
        Assert.assertEquals(
            FsDate.toPattern("10:15:30 +0100"),
            "HH:mm:ss ZZZ"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03T10:15:30"),
            "yyyy-MM-dd'T'HH:mm:ss"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03 10:15:30"),
            "yyyy-MM-dd HH:mm:ss"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03T10:15:30+01:00"),
            "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03T10:15:30 +0100"),
            "yyyy-MM-dd'T'HH:mm:ss ZZZ"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03 10:15:30 +0100"),
            "yyyy-MM-dd HH:mm:ss ZZZ"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03T10:15:30Z"),
            "yyyy-MM-dd'T'HH:mm:ssX"
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03 10:15:30.500"),
            FsDate.PATTERN
        );
        Assert.assertEquals(
            FsDate.toPattern("2011-12-03 10:15:30.500 +0100"),
            FsDate.PATTERN_OFFSET
        );
    }

    @Test
    public void testFormatter() {
        Assert.assertSame(
            FsDate.toFormatter(FsDate.PATTERN),
            FsDate.FORMATTER
        );
        Assert.assertSame(
            FsDate.toFormatter(FsDate.PATTERN_OFFSET),
            FsDate.FORMATTER_OFFSET
        );
        DateTimeFormatter formatter = FsDate.toFormatter("yyyyMMddHHmm");
        Assert.assertSame(
            FsDate.toFormatter("yyyyMMddHHmm"),
            formatter
        );
    }
}
