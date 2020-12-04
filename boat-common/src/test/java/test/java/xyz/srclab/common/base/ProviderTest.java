package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Provider;

import java.util.Arrays;

/**
 * @author sunqian
 */
public class ProviderTest {

    @Test
    public void testProvider() {
        Assert.assertEquals(
                Provider.parseChars(Provider1.class.getName() + ", " + Provider2.class.getName()),
                Arrays.asList(new Provider1(), new Provider2())
        );
        Assert.assertEquals(
                Provider.parseChars(
                        Provider1.class.getName() + ", " + Provider2.class.getName(), true),
                Arrays.asList(new Provider1(), new Provider2())
        );
        Assert.assertEquals(
                Provider.parseCharsFirst(Provider1.class.getName() + ", " + Provider2.class.getName()),
                new Provider1()
        );
        Assert.assertEquals(
                Provider.parseCharsFirst(
                        Provider1.class.getName() + ", " + Provider2.class.getName(), true),
                new Provider1()
        );
        Assert.assertEquals(
                Provider.parseCharsFirst(Provider3.class.getName() + ", " + Provider2.class.getName()),
                new Provider2()
        );
        Assert.assertThrows(IllegalStateException.class, () -> Provider.parseCharsFirst(
                Provider3.class.getName() + ", " + Provider2.class.getName(), true));
        Assert.assertEquals(
                Provider.parseCharsFirstOrNull(Provider3.class.getName()),
                (Object) null
        );
        Assert.assertEquals(
                Provider.parseCharsFirstOrNull(
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
