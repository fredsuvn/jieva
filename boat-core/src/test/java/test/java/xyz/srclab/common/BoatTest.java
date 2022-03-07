package test.java.xyz.srclab.common;

import org.testng.annotations.Test;
import xyz.srclab.common.Boat;
import xyz.srclab.common.base.BLog;

public class BoatTest {

    @Test
    public void testBoat() {
        BLog.info("Boat name: {}", Boat.name());
        BLog.info("Boat version: {}", Boat.version());
        BLog.info("Boat serial version: {}", Boat.serialVersion());
        BLog.info("About Boat: {}", Boat.about());
        BLog.info("Boat secret codes: {}", Boat.INSTANCE.secretCodes());
    }
}
