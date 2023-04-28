package sample.java.xyz.srclab.core.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.Test;
import xyz.srclab.annotations.Immutable;
import xyz.srclab.common.status.Status;
import xyz.srclab.common.status.StringStatus;
import xyz.srclab.common.test.TestLogger;

import java.util.List;

public class StatusSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testState() {
        MyStatus myState = new MyStatus(1, "description");
        MyStatus newState = myState.withMoreMessage("cause");
        //description[cause]
        logger.log(newState.description());
    }

    public static class MyStatus implements Status<Integer, String, MyStatus> {

        private final int code;
        private final List<String> descriptions;

        public MyStatus(int code, @Nullable String description) {
            this.code = code;
            this.descriptions = StringStatus.newDescriptions(description);
        }

        public MyStatus(int code, @Immutable List<String> descriptions) {
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
            return StringStatus.joinDescriptions(descriptions);
        }

        @NotNull
        @Override
        public List<String> descriptions() {
            return descriptions;
        }

        @NotNull
        @Override
        public StatusSample.MyStatus withNewMessage(@Nullable String newDescription) {
            return new MyStatus(code, StringStatus.newDescriptions(newDescription));
        }

        @NotNull
        @Override
        public StatusSample.MyStatus withMoreMessage(String moreDescription) {
            return new MyStatus(code, StringStatus.moreDescriptions(descriptions(), moreDescription));
        }
    }
}
