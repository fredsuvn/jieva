package test.java.xyz.srclab.common.bus;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bus.EventBus;
import xyz.srclab.common.bus.EventHandler;
import xyz.srclab.common.test.TestLogger;
import xyz.srclab.common.utils.Counter;

import java.util.Arrays;

public class EventBusTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEventBus() {
        Counter counter = Counter.startsAt(0, true);
        EventHandler<Integer> h1 = new EventHandler<Integer>() {
            @NotNull
            @Override
            public Object eventType() {
                return 1;
            }

            @Override
            public void handle(@NotNull Integer event) {
                logger.log("Now counter is: {}, after add: {}", counter.getInt(), counter.addAndGetInt(event));
            }
        };
        EventHandler<Integer> h2 = new EventHandler<Integer>() {
            @NotNull
            @Override
            public Object eventType() {
                return 2;
            }

            @Override
            public void handle(@NotNull Integer event) {
                logger.log("Now counter is: {}, after add: {}", counter.getInt(), counter.addAndGetInt(event));
            }
        };
        EventHandler<Integer> h3 = new EventHandler<Integer>() {
            @NotNull
            @Override
            public Object eventType() {
                return 3;
            }

            @Override
            public void handle(@NotNull Integer event) {
                logger.log("Now counter is: {}, after add: {}", counter.getInt(), counter.addAndGetInt(event));
            }
        };
        EventBus eventBus = EventBus.newEventBus(Arrays.asList(h1, h2, h3));
        eventBus.emit(3, 30);
        eventBus.emit(2, 20);
        eventBus.emit(1, 10);
        Assert.assertEquals(counter.getInt(), 60);
    }
}
