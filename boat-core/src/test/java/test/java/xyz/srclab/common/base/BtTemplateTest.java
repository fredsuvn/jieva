package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtLog;
import xyz.srclab.common.base.BtTemplate;
import xyz.srclab.common.base.StringTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public class BtTemplateTest {

    @Test
    public void testTemplate() {
        //Using named ars:
        Map<String, Object> args = new HashMap<>();
        args.put("n1", "Dog");
        args.put("n2", "Cat");
        StringTemplate template1 = BtTemplate.parse("This is a {n1}, that is a {n2}", null, "{", "}");
        BtLog.info("{}", template1.process(args));
        Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");

        //Using unnamed args:
        StringTemplate template2 = BtTemplate.parse("This is a {}, that is a {}", null, "{", "}");
        BtLog.info("{}", template2.process(args));
        Assert.assertEquals(template2.processArgs("Dog", "Cat"), "This is a Dog, that is a Cat");

        //Using only prefix:
        StringTemplate template3 = BtTemplate.parse("This is a $n1, that is a $n2", null, "$");
        BtLog.info("{}", template3.process(args));
        Assert.assertEquals(template3.process(args), "This is a Dog, that is a Cat");
        StringTemplate template4 = BtTemplate.parse("This is a $, that is a $", null, "$");
        BtLog.info("{}", template4.process(args));
        Assert.assertEquals(template4.processArgs("Dog", "Cat"), "This is a Dog, that is a Cat");

        //Escape:
        StringTemplate template5 = BtTemplate.parse("This \\is \\\\a} \\{{n1}, that is a {n2}\\", '\\', "{", "}");
        BtLog.info("{}", template5.process(args));
        Assert.assertEquals(template5.process(args), "This \\is \\a} {Dog, that is a Cat\\");
    }
}
