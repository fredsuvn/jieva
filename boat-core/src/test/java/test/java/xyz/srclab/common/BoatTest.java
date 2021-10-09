package test.java.xyz.srclab.common;

import org.testng.annotations.Test;
import xyz.srclab.common.Boat;
import xyz.srclab.common.logging.Logs;

public class BoatTest {

    @Test
    public void testBoat() {
        Logs.info("About Boat: {}", Boat.ABOUT);
    }
}
