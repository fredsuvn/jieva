package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BTemplate;
import xyz.srclab.common.logging.Logs;

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
        args.put("name}", "DogX");
        args.put(1, "Cat");
        args.put(2, "Bird");
        BTemplate template1 = BTemplate.resolve(
            "This is a {name}, that is a {}", "{", "}");
        Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");
        BTemplate template2 = BTemplate.resolve(
            "This is a } {name}, that is a {}}", "{", "}");
        Assert.assertEquals(template2.process(args), "This is a } Dog, that is a Cat}");
        BTemplate template3 = BTemplate.resolve(
            "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\", "{", "}", "\\");
        Logs.info(template3.process(args));
        Assert.assertEquals(template3.process(args), "This is a } {DogX (Dog), that is a Bird\\{");
    }
}
