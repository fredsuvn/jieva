package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.CharsTemplate;
import xyz.srclab.common.test.TestLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public class CharsTemplateTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testTemplate() {
        Map<Object, Object> args = new HashMap<>();
        args.put("name", "Dog");
        args.put("name}", "DogX");
        args.put(1, "Cat");
        args.put(2, "Bird");
        CharsTemplate template1 = CharsTemplate.resolve(
            "This is a {name}, that is a {}", "{", "}");
        Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");
        CharsTemplate template2 = CharsTemplate.resolve(
            "This is a } {name}, that is a {}}", "{", "}");
        Assert.assertEquals(template2.process(args), "This is a } Dog, that is a Cat}");
        CharsTemplate template3 = CharsTemplate.resolve(
            "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\", "{", "}", "\\");
        logger.log(template3.process(args));
        Assert.assertEquals(template3.process(args), "This is a } {DogX (Dog), that is a Bird\\{");
    }
}
