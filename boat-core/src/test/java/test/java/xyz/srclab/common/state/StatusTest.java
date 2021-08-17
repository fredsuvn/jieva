package test.java.xyz.srclab.common.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.annotations.Immutable;
import xyz.srclab.common.status.Status;
import xyz.srclab.common.status.StringStatus;
import xyz.srclab.common.test.TestLogger;

import java.util.List;

public class StatusTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testState() {
        TestStatus testState = new TestStatus(1, "description");
        TestStatus newState = testState.withMoreDescription("cause");
        TestStatus newNewState = newState.withMoreDescription("cause2");
        //description[cause][cause2]
        logger.log(newNewState.description());
        Assert.assertEquals(newNewState.description(), "description[cause][cause2]");
    }

    public static class TestStatus implements Status<Integer, String, TestStatus> {

        private final int code;
        private final List<String> descriptions;

        public TestStatus(int code, @Nullable String description) {
            this.code = code;
            this.descriptions = StringStatus.newDescriptions(description);
        }

        public TestStatus(int code, @Immutable List<String> descriptions) {
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
        public StatusTest.TestStatus withNewDescription(@Nullable String newDescription) {
            return new TestStatus(code, StringStatus.newDescriptions(newDescription));
        }

        @NotNull
        @Override
        public StatusTest.TestStatus withMoreDescription(String moreDescription) {
            return new TestStatus(code, StringStatus.moreDescriptions(descriptions(), moreDescription));
        }
    }
}
