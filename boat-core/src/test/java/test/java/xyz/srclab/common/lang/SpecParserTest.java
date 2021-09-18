package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.SpecParser;
import xyz.srclab.common.base.SpecParsingException;

import java.util.Arrays;

/**
 * @author sunqian
 */
public class SpecParserTest {

    @Test
    public void testProvider() {
        Assert.assertEquals(
            SpecParser.parseClassNameToInstance(
                Provider1.class.getName() + ", " + Provider2.class.getName()),
            Arrays.asList(new Provider1(), new Provider2())
        );
        Assert.assertEquals(
            SpecParser.parseClassNameToInstance(
                Provider1.class.getName() + ", " + Provider2.class.getName(), true),
            Arrays.asList(new Provider1(), new Provider2())
        );
        Assert.assertEquals(
            SpecParser.parseFirstClassNameToInstance(
                Provider1.class.getName() + ", " + Provider2.class.getName()),
            new Provider1()
        );
        Assert.assertEquals(
            SpecParser.parseFirstClassNameToInstance(
                Provider1.class.getName() + ", " + Provider2.class.getName(), true),
            new Provider1()
        );
        Assert.assertEquals(
            SpecParser.parseFirstClassNameToInstance(
                Provider3.class.getName() + ", " + Provider2.class.getName()),
            new Provider2()
        );
        Assert.assertThrows(SpecParsingException.class, () -> SpecParser.parseFirstClassNameToInstance(
            Provider3.class.getName() + ", " + Provider2.class.getName(), true));
        Assert.assertEquals(
            SpecParser.parseFirstClassNameToInstanceOrNull(Provider3.class.getName()),
            (Object) null
        );
        Assert.assertEquals(
            SpecParser.parseFirstClassNameToInstanceOrNull(
                Provider3.class.getName() + ", " + Provider2.class.getName()),
            new Provider2()
        );
    }

    public static class Provider1 {
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Provider1;
        }
    }

    public static class Provider2 {
        @Override
        public int hashCode() {
            return 2;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Provider2;
        }
    }

    public static class Provider3 {
        public Provider3() {
            throw new IllegalStateException();
        }
    }
}
