package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsDate;
import xyz.srclab.common.base.FsLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    }

    @Test
    public void testISO() throws ParseException {
        Date now = new Date();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZZZ").format(now);
        FsLogger.system().info(dateStr);
        FsLogger.system().info(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSZZZ").format(ZonedDateTime.now()));
        FsLogger.system().info(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ")
            .format(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)));
    }
}
