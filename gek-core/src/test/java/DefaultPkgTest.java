import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.reflect.JieReflect;

public class DefaultPkgTest {

    @Test
    public void testLastName() {
        Assert.assertEquals(JieReflect.getLastName(DefaultPkgTest.class), DefaultPkgTest.class.getName());
    }
}
