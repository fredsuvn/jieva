import org.testng.annotations.Test;
import xyz.fslabo.common.reflect.JieReflect;

import static org.testng.Assert.assertEquals;

public class DefaultTest {

    @Test
    public void testLastName() {
        assertEquals(JieReflect.getLastName(DefaultTest.class), DefaultTest.class.getName());
    }
}
