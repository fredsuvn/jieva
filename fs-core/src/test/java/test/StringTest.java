package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.StringAppender;

public class StringTest {

    @Test
    public void test() {
        StringAppender appender = new StringAppender();
        String word = appender
            .append('h')
            .append('e')
            .append("llo")
            .append(' ')
            .append('w')
            .append("orld")
            .append('!')
            .toString();
        Assert.assertEquals(word, "hello world!");
    }
}
