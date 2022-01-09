package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BTemplate;
import xyz.srclab.common.base.StringTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public class BTemplateTest {

    @Test
    public void testTemplate() {
        Map<Object, Object> args = new HashMap<>();
        args.put("name", "Dog");
        args.put("name\\", "DogX");
        args.put(1, "Cat");
        args.put(2, "Bird");
        StringTemplate template1 = BTemplate.parse(
            "This is a {name}, that is a {}", '\\', "{", "}");
        Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");
        StringTemplate template2 = BTemplate.parse(
            "This is a } {name}, that is a {}}", '\\', "{", "}");
        Assert.assertEquals(template2.process(args), "This is a } Dog, that is a Cat}");
        StringTemplate template3 = BTemplate.parse(
            "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\", '\\', "{", "}");
        BLog.info(template3.process(args));
        Assert.assertEquals(template3.process(args), "This is a } {DogX} (Dog), that is a Bird\\{\\");
    }
}
