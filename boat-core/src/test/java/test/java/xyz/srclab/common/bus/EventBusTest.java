package test.java.xyz.srclab.common.bus;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.StringRef;
import xyz.srclab.common.bus.EventBus;
import xyz.srclab.common.bus.EventBusHandler;
import xyz.srclab.common.collect.BList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBusTest {

    private static final List<CharSequence> chars = new CopyOnWriteArrayList<>();

    @Test
    public void testEventBus() {
        EventBus eventBus = EventBus.newEventBus();
        eventBus.registerAll(BList.newList(new CharSeqHandler(), new StringHandler(), new IntegerHandler()));

        eventBus.post("x");
        eventBus.post(StringRef.of("y"));
        eventBus.post(6);

        Assert.assertEquals(chars, BList.newList("xsxy7"));
    }

    public static class StringHandler implements EventBusHandler<String> {

        @NotNull
        @Override
        public Class<String> getEventType() {
            return String.class;
        }

        @Override
        public void doEvent(String event) {
            chars.add(event + "s");
        }
    }

    public static class CharSeqHandler implements EventBusHandler<CharSequence> {

        @NotNull
        @Override
        public Class<CharSequence> getEventType() {
            return CharSequence.class;
        }

        @Override
        public void doEvent(CharSequence event) {
            chars.add(event.toString());
        }
    }

    public static class IntegerHandler implements EventBusHandler<Integer> {

        @NotNull
        @Override
        public Class<Integer> getEventType() {
            return Integer.class;
        }

        @Override
        public void doEvent(Integer event) {
            chars.add(String.valueOf(event + 1));
        }
    }
}
