package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;

import java.util.Arrays;
import java.util.List;

public class StringTest {

    @Test
    public void test() {
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
}
