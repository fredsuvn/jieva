package test.xyz.srclab.common.egg;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.egg.Egg;
import xyz.srclab.common.egg.NoSuchEggException;
import xyz.srclab.common.egg.HelloEgg;
import xyz.srclab.common.egg.WrongMagicException;
import xyz.srclab.common.lang.Defaults;
import xyz.srclab.common.test.TestLogger;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public class EggTest {

    private static TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEgg() throws Exception {
        Assert.expectThrows(NoSuchEggException.class, () -> {
            Egg.pick("Hello, Egg!");
        });

        Egg hello = Egg.pick(HelloEgg.class.getName());
        Assert.expectThrows(WrongMagicException.class, () -> {
            hello.hatchOut("123456", Collections.emptyMap());
        });

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        Map<Object, Object> feed = Collects.newMap(new HashMap<>(), "out", printStream);
        hello.hatchOut("Hello, Boat!", feed);
        String eggMessage = outputStream.toString(Defaults.charset().name());
        Assert.assertEquals(eggMessage.trim(), "Hello, Boat!");
    }

    @Test
    public void testOSpaceBattle() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        Egg egg = Egg.pick("xyz.srclab.common.egg.nest.o.OBattle");
        egg.hatchOut("Thank you, Taro.", Collections.emptyMap());
        //Or
        //egg.hatchOut("谢谢你，泰罗。", Collections.emptyMap());
    }

    public static void main(String[] args) {
        new EggTest().testOSpaceBattle();
    }
}
