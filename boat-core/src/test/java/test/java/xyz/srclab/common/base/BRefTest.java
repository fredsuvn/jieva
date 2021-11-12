package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BRef;
import xyz.srclab.common.logging.Logs;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BRefTest {

    @Test
    public void testRef() {
        BRef<String> ref = BRef.of(null);
        Assert.assertEquals(ref.getOrElse("null"), "null");
        Assert.assertFalse(ref.isPresent());
        ref.set("123");
        Assert.assertEquals(ref.getOrElse("null"), "123");
        Assert.assertTrue(ref.isPresent());
    }

    @Test
    public void testChainOps() {
        String dateString = "2222-12-22 22:22:22";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Date date = BRef.of(dateString)
            .accept(Logs::info)
            .apply(d -> LocalDateTime.parse(d, formatter))
            .apply(d -> d.atZone(ZoneId.systemDefault()))
            .with(ChronoZonedDateTime::toInstant)
            .with(Date::from)
            .get();
        Logs.info("date: {}", date);
    }
}
