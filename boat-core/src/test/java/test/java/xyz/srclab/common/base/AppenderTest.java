package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtRandom;
import xyz.srclab.common.base.BtString;
import xyz.srclab.common.base.StringAppender;

/**
 * @author sunqian
 */
public class AppenderTest {

    @Test
    public void testStringAppender() {
        StringAppender stringAppender = new StringAppender();
        StringBuilder stringBuilder = new StringBuilder();
        String msg = BtRandom.randomString(100);
        for (int i = 0; i < 20; i++) {
            stringAppender.append(msg);
            stringBuilder.append(msg);
        }
        for (int i = 0; i < 20; i++) {
            stringAppender.append(msg, 8, 88);
            stringBuilder.append(msg, 8, 88);
        }
        for (int i = 0; i < 20; i++) {
            stringAppender.append(BtString.subRef(msg, 10, 30));
            stringBuilder.append(BtString.subRef(msg, 10, 30));
        }
        for (int i = 0; i < 20; i++) {
            stringAppender.append(msg, 10);
            stringBuilder.append(msg, 10, msg.length());
        }
        for (int i = 0; i < 20; i++) {
            stringAppender.append(msg, 10, 30);
            stringBuilder.append(msg, 10, 30);
        }
        for (int i = 0; i < 20; i++) {
            stringAppender.write(msg.substring(10, 30).toCharArray());
            stringBuilder.append(BtString.subRef(msg, 10, 30));
        }
        Object obj = new Object();
        stringAppender.append(obj);
        stringBuilder.append(obj);
        Assert.assertEquals(stringAppender.toString(), stringBuilder.toString());
        stringAppender.clear();
        Assert.assertEquals(stringAppender.toString(), "");
    }
}
