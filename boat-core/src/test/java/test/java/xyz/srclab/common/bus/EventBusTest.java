package test.java.xyz.srclab.common.bus;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bus.EventBus;
import xyz.srclab.common.bus.SubscribeMethod;
import xyz.srclab.common.lang.Next;
import xyz.srclab.common.test.TestLogger;

import java.util.Arrays;

public class EventBusTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEventBus() {
        EventBus eventBus = EventBus.newEventBus();
        Handler1 handler1 = new Handler1();
        eventBus.register(handler1);
        eventBus.post("123");
        eventBus.post(new Object());
        Assert.assertEquals(handler1.stack, "sub12sub11");
        eventBus.unregister(handler1);

        Handler2 handler2 = new Handler2();
        eventBus.register(handler2);
        eventBus.post("123");
        Assert.assertEquals(handler1.stack, "sub12sub11");
        Assert.assertEquals(handler2.stack, "sub23sub22");
        eventBus.unregister(handler2);

        eventBus.registerAll(Arrays.asList(handler1, handler2));
        eventBus.post("456");
        Assert.assertEquals(handler1.stack, "sub12sub11sub12sub11");
        Assert.assertEquals(handler2.stack, "sub23sub22sub23sub22");
    }

    public static class Handler1 {

        public String stack = "";

        @SubscribeMethod
        public void sub11(CharSequence chars) {
            logger.log("sub11:" + chars);
            stack += "sub11";
        }

        @SubscribeMethod(priority = 100)
        public void sub12(String chars) {
            logger.log("sub12:" + chars);
            stack += "sub12";
        }
    }

    public static class Handler2 {

        public String stack = "";

        @SubscribeMethod
        public void sub20(Integer integer) {
            logger.log("sub20:" + integer);
            stack += "sub20";
        }

        @SubscribeMethod
        public void sub21(String chars) {
            logger.log("sub21:" + chars);
            stack += "sub21";
        }

        @SubscribeMethod(priority = 100)
        public Next sub22(String chars) {
            logger.log("sub22:" + chars);
            stack += "sub22";
            return Next.BREAK;
        }

        @SubscribeMethod(priority = 200)
        public void sub23(String chars) {
            logger.log("sub23:" + chars);
            stack += "sub23";
        }

        @SubscribeMethod(priority = 300)
        public void sub24(Integer integer) {
            logger.log("sub20:" + integer);
            stack += "sub20";
        }
    }
}
