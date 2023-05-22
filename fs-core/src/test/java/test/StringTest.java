package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringTest {

    @Test
    public void testConcat() {
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
    public void testStartWith() {
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

    @Test
    public void testIndexOf() {
        Assert.assertEquals(
            FsString.indexOf("1234567890", "2"),
            1
        );
        Assert.assertEquals(
            FsString.lastIndexOf("1234567890", "2"),
            1
        );
        Assert.assertEquals(
            FsString.indexOf("12345678901234567890", "2", 9),
            11
        );
        Assert.assertEquals(
            FsString.lastIndexOf("12345678901234567890", "2", 9),
            1
        );
        Assert.assertEquals(
            FsString.indexOf("11", "11"),
            0
        );
        Assert.assertEquals(
            FsString.lastIndexOf("11", "11"),
            0
        );
        Assert.assertEquals(
            FsString.indexOf("", ""),
            -1
        );
        Assert.assertEquals(
            FsString.indexOf("1", "11"),
            -1
        );
        Assert.assertEquals(
            FsString.lastIndexOf("", ""),
            -1
        );
        Assert.assertEquals(
            FsString.lastIndexOf("1", "11"),
            -1
        );
    }

    @Test
    public void testSplit() {
        Assert.assertEquals(
            FsString.split("123--123--123--", "--"),
            Arrays.asList("123", "123", "123", "")
        );
        Assert.assertEquals(
            FsString.split("123", "1234"),
            Collections.emptyList()
        );
        Assert.assertEquals(
            FsString.split("", "1234"),
            Collections.emptyList()
        );
        Assert.assertEquals(
            FsString.split("123", "123"),
            Arrays.asList("", "")
        );
        Assert.assertEquals(
            FsString.split("123--123--123----", "--"),
            Arrays.asList("123", "123", "123", "", "")
        );
        Assert.assertEquals(
            FsString.split("--123--123--123----", "--"),
            Arrays.asList("", "123", "123", "123", "", "")
        );
    }
}
