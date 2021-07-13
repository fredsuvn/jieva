package sample.java.xyz.srclab.core.bus;

import org.testng.annotations.Test;
import xyz.srclab.common.bus.EventBus;
import xyz.srclab.common.bus.SubscribeMethod;
import xyz.srclab.common.lang.Next;
import xyz.srclab.common.test.TestLogger;

public class EventBusSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEventBus() {
        EventBus eventBus = EventBus.newEventBus();
        Handler1 handler1 = new Handler1();
        eventBus.register(handler1);
        eventBus.post("123");
        //sub3sub2sub0 or sub0sub3sub2
        logger.log("subs: " + handler1.stack);
        eventBus.unregister(handler1);
    }

    public static class Handler1 {

        public String stack = "";

        @SubscribeMethod(priority = 100)
        public void sub0(CharSequence chars) {
            logger.log("sub0:" + chars);
            stack += "sub0";
        }

        @SubscribeMethod
        public void sub1(String chars) {
            logger.log("sub1:" + chars);
            stack += "sub1";
        }

        @SubscribeMethod(priority = 100)
        public Next sub2(String chars) {
            logger.log("sub2:" + chars);
            stack += "sub2";
            return Next.BREAK;
        }

        @SubscribeMethod(priority = 200)
        public void sub3(String chars) {
            logger.log("sub3:" + chars);
            stack += "sub3";
        }
    }
}
