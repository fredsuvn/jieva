package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BTemplate;
import xyz.srclab.common.base.CharsTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public class BTemplateTest {

    @Test
    public void testTemplate() {
        //Using named ars:
        Map<String, Object> args = new HashMap<>();
        args.put("n1", "Dog");
        args.put("n2", "Cat");
        CharsTemplate template1 = BTemplate.parse("This is a {n1}, that is a {n2}", null, "{", "}");
        BLog.info("{}", template1.process(args));
        Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");

        //Using unnamed args:
        CharsTemplate template2 = BTemplate.parse("This is a {}, that is a {}", null, "{", "}");
        BLog.info("{}", template2.process(args));
        Assert.assertEquals(template2.processArgs("Dog", "Cat"), "This is a Dog, that is a Cat");

        //Using only prefix:
        CharsTemplate template3 = BTemplate.parse("This is a $n1, that is a $n2", null, "$");
        BLog.info("{}", template3.process(args));
        Assert.assertEquals(template3.process(args), "This is a Dog, that is a Cat");
        CharsTemplate template4 = BTemplate.parse("This is a $, that is a $", null, "$");
        BLog.info("{}", template4.process(args));
        Assert.assertEquals(template4.processArgs("Dog", "Cat"), "This is a Dog, that is a Cat");

        //Escape:
        CharsTemplate template5 = BTemplate.parse("This \\is \\\\a} \\{{n1}, that is a {n2}\\", '\\', "{", "}");
        BLog.info("{}", template5.process(args));
        Assert.assertEquals(template5.process(args), "This \\is \\a} {Dog, that is a Cat\\");
    }
}
