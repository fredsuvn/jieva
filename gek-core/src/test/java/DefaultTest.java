import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.reflect.JieReflect;

public class DefaultTest {

    @Test
    public void testLastName() {
        Assert.assertEquals(JieReflect.getLastName(DefaultTest.class), DefaultTest.class.getName());
    }
}
