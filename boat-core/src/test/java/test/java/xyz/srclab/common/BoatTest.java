package test.java.xyz.srclab.common;

import org.testng.annotations.Test;
import xyz.srclab.common.Boat;
import xyz.srclab.common.base.BLog;

public class BoatTest {

    @Test
    public void testBoat() {
        BLog.info("About Boat: {}", Boat.ABOUT);
    }
}
