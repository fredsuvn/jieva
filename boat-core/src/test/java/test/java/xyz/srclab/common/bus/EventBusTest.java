package test.java.xyz.srclab.common.bus;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bus.EventBus;
import xyz.srclab.common.bus.SubscribeMethod;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.logging.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EventBusTest {

    private static final Logger logger = Logger.simpleLogger();

    @Test
    public void testEventBus() {
        EventBus eventBus = EventBus.newEventBus();
        Handler1 handler1 = new Handler1();
        eventBus.register(handler1);
        eventBus.post("123");
        eventBus.post(new Object());
        Assert.assertTrue(Collects.newSet("sub11", "sub12").containsAll(handler1.stack));
        Assert.assertTrue(handler1.stack.containsAll(Collects.newSet("sub11", "sub12")));
        eventBus.unregister(handler1);

        Handler2 handler2 = new Handler2();
        eventBus.register(handler2);
        eventBus.post("123");
        Assert.assertTrue(Collects.newSet("sub11", "sub12").containsAll(handler1.stack));
        Assert.assertTrue(Collects.newSet("sub21", "sub22", "sub23").containsAll(handler2.stack));
        eventBus.unregister(handler2);

        eventBus.registerAll(Arrays.asList(handler1, handler2));
        eventBus.post("456");
        Assert.assertTrue(Collects.newSet("sub11", "sub12").containsAll(handler1.stack));
        Assert.assertTrue(Collects.newSet("sub21", "sub22", "sub23").containsAll(handler2.stack));
    }

    public static class Handler1 {

        public Set<String> stack = new HashSet<>();

        @SubscribeMethod
        public void sub11(CharSequence chars) {
            logger.info("sub11:" + chars);
            stack.add("sub11");
        }

        @SubscribeMethod
        public void sub12(String chars) {
            logger.info("sub12:" + chars);
            stack.add("sub12");
        }
    }

    public static class Handler2 {

        public Set<String> stack = new HashSet<>();

        @SubscribeMethod
        public void sub20(Integer integer) {
            logger.info("sub20:" + integer);
            stack.add("sub20");
        }

        @SubscribeMethod
        public void sub21(String chars) {
            logger.info("sub21:" + chars);
            stack.add("sub21");
        }

        @SubscribeMethod
        public void sub22(String chars) {
            logger.info("sub22:" + chars);
            stack.add("sub22");
        }

        @SubscribeMethod
        public void sub23(String chars) {
            logger.info("sub23:" + chars);
            stack.add("sub23");
        }

        @SubscribeMethod
        public void sub24(Integer integer) {
            logger.info("sub20:" + integer);
            stack.add("sub20");
        }
    }
}
