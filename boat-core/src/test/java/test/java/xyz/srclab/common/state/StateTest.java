package test.java.xyz.srclab.common.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.annotations.Immutable;
import xyz.srclab.common.state.CharsState;
import xyz.srclab.common.state.State;
import xyz.srclab.common.test.TestLogger;

import java.util.List;

public class StateTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testState() {
        TestState testState = new TestState(1, "description");
        TestState newState = testState.withMoreDescription("cause");
        TestState newNewState = newState.withMoreDescription("cause2");
        //description[cause][cause2]
        logger.log(newNewState.description());
        Assert.assertEquals(newNewState.description(), "description[cause][cause2]");
    }

    public static class TestState implements State<Integer, String, TestState> {

        private final int code;
        private final List<String> descriptions;

        public TestState(int code, @Nullable String description) {
            this.code = code;
            this.descriptions = CharsState.newDescriptions(description);
        }

        public TestState(int code, @Immutable List<String> descriptions) {
            this.code = code;
            this.descriptions = descriptions;
        }

        @Override
        public Integer code() {
            return code;
        }

        @Nullable
        @Override
        public String description() {
            return CharsState.joinDescriptions(descriptions);
        }

        @NotNull
        @Override
        public List<String> descriptions() {
            return descriptions;
        }

        @NotNull
        @Override
        public StateTest.TestState withNewDescription(@Nullable String newDescription) {
            return new TestState(code, CharsState.newDescriptions(newDescription));
        }

        @NotNull
        @Override
        public StateTest.TestState withMoreDescription(String moreDescription) {
            return new TestState(code, CharsState.moreDescriptions(descriptions(), moreDescription));
        }
    }
}
