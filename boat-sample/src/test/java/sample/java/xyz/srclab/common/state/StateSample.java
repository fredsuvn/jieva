package sample.java.xyz.srclab.common.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.Test;
import xyz.srclab.common.state.State;
import xyz.srclab.common.test.TestLogger;

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
        private final String description;

        public MyState(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Override
        public Integer code() {
            return code;
        }

        @Nullable
        @Override
        public String description() {
            return description;
        }

        @NotNull
        @Override
        public MyState withNewDescription(@Nullable String newDescription) {
            return new MyState(code, newDescription);
        }

        @NotNull
        @Override
        public MyState withMoreDescription(@Nullable String moreDescription) {
            return new MyState(code, State.moreDescription(description, moreDescription));
        }
    }
}
