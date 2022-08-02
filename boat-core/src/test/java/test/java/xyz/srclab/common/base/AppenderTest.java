package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtRandom;
import xyz.srclab.common.base.BtString;
import xyz.srclab.common.base.CharsBuilder;

/**
 * @author sunqian
 */
public class AppenderTest {

    @Test
    public void testStringAppender() {
        CharsBuilder charsBuilder = new CharsBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        String msg = BtRandom.randomString(100);
        for (int i = 0; i < 20; i++) {
            charsBuilder.append(msg);
            stringBuilder.append(msg);
        }
        for (int i = 0; i < 20; i++) {
            charsBuilder.append(msg, 8, 88);
            stringBuilder.append(msg, 8, 88);
        }
        for (int i = 0; i < 20; i++) {
            charsBuilder.append(BtString.subRef(msg, 10, 30));
            stringBuilder.append(BtString.subRef(msg, 10, 30));
        }
        for (int i = 0; i < 20; i++) {
            charsBuilder.append(msg, 10);
            stringBuilder.append(msg, 10, msg.length());
        }
        for (int i = 0; i < 20; i++) {
            charsBuilder.append(msg, 10, 30);
            stringBuilder.append(msg, 10, 30);
        }
        for (int i = 0; i < 20; i++) {
            charsBuilder.write(msg.substring(10, 30).toCharArray());
            stringBuilder.append(BtString.subRef(msg, 10, 30));
        }
        Object obj = new Object();
        charsBuilder.append(obj);
        stringBuilder.append(obj);
        Assert.assertEquals(charsBuilder.toString(), stringBuilder.toString());
        charsBuilder.clear();
        Assert.assertEquals(charsBuilder.toString(), "");
    }
}
