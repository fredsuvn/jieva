package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.convert.FsConvert;

import java.time.Instant;
import java.util.Date;

public class ConvertTest {

    @Test
    public void testConvert() {
        long now = System.currentTimeMillis();
        Assert.assertEquals(
            Instant.ofEpochMilli(now),
            FsConvert.convert(new Date(now), Instant.class)
        );
    }
}
