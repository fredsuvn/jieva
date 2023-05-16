package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;

import java.util.Arrays;
import java.util.List;

public class StringTest {

    @Test
    public void concat() {
        List<String> list = Arrays.asList(
            "dsfasfas",
            "fsafs",
            "fasdf",
            "fas",
            "fdfsf",
            "fsafsaf"
        );
        Assert.assertEquals(
            FsString.concat(list.toArray()),
            String.join("", list)
        );
        Assert.assertEquals(
            FsString.concat(list),
            String.join("", list)
        );
    }

    @Test
    public void startWith() {
        String a = "123abc123";
        Assert.assertEquals(
            FsString.startWith(a, "456"),
            "456" + a
        );
        Assert.assertEquals(
            FsString.startWith(a, "123"),
            a
        );
        Assert.assertEquals(
            FsString.endWith(a, "456"),
            a + "456"
        );
        Assert.assertEquals(
            FsString.endWith(a, "123"),
            a
        );
        Assert.assertEquals(
            FsString.removeStart(a, "456"),
            a
        );
        Assert.assertEquals(
            FsString.removeStart(a, "123"),
            "abc123"
        );
        Assert.assertEquals(
            FsString.removeEnd(a, "456"),
            a
        );
        Assert.assertEquals(
            FsString.removeEnd(a, "123"),
            "123abc"
        );
    }
}
