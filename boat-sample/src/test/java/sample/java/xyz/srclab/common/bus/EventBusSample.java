package sample.java.xyz.srclab.common.bus;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.srclab.common.bus.EventBus;
import xyz.srclab.common.bus.EventHandler;
import xyz.srclab.common.bus.EventHandlerNotFoundException;
import xyz.srclab.common.test.TestLogger;

import java.util.Arrays;

public class EventBusSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEventBus() {
        EventBus eventBus = EventBus.newEventBus(Arrays.asList(
            new EventHandler<Object>() {
                @NotNull
                @Override
                public Object eventType() {
                    return String.class;
                }

                @Override
                public void handle(@NotNull Object event) {
                    logger.log(event);
                }
            },
            new EventHandler<Object>() {
                @NotNull
                @Override
                public Object eventType() {
                    return Integer.class;
                }

                @Override
                public void handle(@NotNull Object event) {
                    logger.log(event);
                }
            }
        ));
        //1
        eventBus.emit(1);
        //2
        eventBus.emit("2");
        //No output
        eventBus.emit(new Object());
        try {
            eventBus.emitOrThrow(new Object());
        } catch (EventHandlerNotFoundException e) {
            //xyz.srclab.common.bus.EventHandlerNotFoundException: class java.lang.Object
            logger.log(e);
        }
    }
}
