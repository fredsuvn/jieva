package test.java.xyz.srclab.common.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.status.IntStringStatus;
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

    public static class TestStatus extends IntStringStatus {

        public TestStatus(int code, @Nullable String description) {
            super(code, description);
        }

        public TestStatus(int code, @NotNull List<String> descriptions) {
            super(code, descriptions);
        }

        public TestStatus(IntStringStatus intStringStatus) {
            super(intStringStatus.code(), intStringStatus.descriptions());
        }

        @NotNull
        @Override
        public TestStatus withNewDescription(@Nullable String newDescription) {
            return new TestStatus(super.withNewDescription(newDescription));
        }

        @NotNull
        @Override
        public TestStatus withMoreDescription(@NotNull String moreDescription) {
            return new TestStatus(super.withMoreDescription(moreDescription));
        }
    }
}
