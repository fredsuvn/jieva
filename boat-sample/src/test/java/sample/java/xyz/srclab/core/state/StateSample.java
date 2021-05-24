package sample.java.xyz.srclab.core.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.Test;
import xyz.srclab.annotations.Immutable;
import xyz.srclab.common.state.CharsState;
import xyz.srclab.common.state.State;
import xyz.srclab.common.test.TestLogger;

import java.util.List;

public class StateSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testState() {
        MyState myState = new MyState(1, "description");
        MyState newState = myState.withMoreDescription("cause");
        //description[cause]
        logger.log(newState.description());
    }

    public static class MyState implements State<Integer, String, MyState> {

        private final int code;
        private final List<String> descriptions;

        public MyState(int code, @Nullable String description) {
            this.code = code;
            this.descriptions = CharsState.newDescriptions(description);
        }

        public MyState(int code, @Immutable List<String> descriptions) {
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
        public MyState withNewDescription(@Nullable String newDescription) {
            return new MyState(code, CharsState.newDescriptions(newDescription));
        }

        @NotNull
        @Override
        public MyState withMoreDescription(String moreDescription) {
            return new MyState(code, CharsState.moreDescriptions(descriptions(), moreDescription));
        }
    }
}
