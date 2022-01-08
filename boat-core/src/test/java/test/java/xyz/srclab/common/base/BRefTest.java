package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.Ref;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BRefTest {

    @Test
    public void testRef() {
        Ref<String> ref = Ref.of(null);
        Assert.assertEquals(ref.orDefault("null"), "null");
        Assert.assertFalse(ref.isPresent());
        ref.set("123");
        Assert.assertEquals(ref.orDefault("null"), "123");
        Assert.assertTrue(ref.isPresent());
    }

    @Test
    public void testChainOps() {
        String dateString = "2222-12-22 22:22:22";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Date date = Ref.of(dateString)
            .accept(BLog::info)
            .map(d -> LocalDateTime.parse(d, formatter))
            .map(d -> d.atZone(ZoneId.systemDefault()))
            .map(ChronoZonedDateTime::toInstant)
            .map(Date::from)
            .get();
        BLog.info("date: {}", date);
    }
}
