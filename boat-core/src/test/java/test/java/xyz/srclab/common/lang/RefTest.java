package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Ref;
import xyz.srclab.common.test.TestLogger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class RefTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testRef() {
        Ref<String> ref = Ref.of(null);
        Assert.assertEquals(ref.getOrElse("null"), "null");
        Assert.assertFalse(ref.isPresent());
        ref.set("123");
        Assert.assertEquals(ref.getOrElse("null"), "123");
        Assert.assertTrue(ref.isPresent());
    }

    @Test
    public void testChainOps() {
        String dateString = "22222-2-22 22:22:22";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Date date = Ref.of(dateString)
            .accept(logger::log)
            .apply(d -> LocalDateTime.parse(d, formatter))
            .apply(d -> d.atZone(ZoneId.systemDefault()))
            .apply(ChronoZonedDateTime::toInstant)
            .apply(Date::from)
            .get();
        logger.log(date);
    }
}
