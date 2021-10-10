package test.java.xyz.srclab.common.status;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.logging.Logs;
import xyz.srclab.common.status.IntStringStatus;
import xyz.srclab.common.status.Status;

import java.util.List;

public class StatusTest {

    @Test
    public void testState() {
        TestStatus testState = new TestStatus(1, "description");
        TestStatus newState = testState.withMoreDescription("cause");
        TestStatus newNewState = newState.withMoreDescription("cause2");
        //description[cause][cause2]
        Logs.info("newNewState.getDescription: {}", newNewState.getDescription());
        Assert.assertEquals(newNewState.getDescription(), "description[cause][cause2]");
    }

    public static class TestStatus extends IntStringStatus {

        public TestStatus(int code, @Nullable String description) {
            super(code, description);
        }

        public TestStatus(int code, @NotNull List<String> descriptions) {
            super(code, descriptions);
        }

        public TestStatus(Status<Integer, String> intStringStatus) {
            super(intStringStatus.getCode(), intStringStatus.getDescription());
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

        @NotNull
        @Override
        public Integer getCode() {
            return super.getCode();
        }

        @Nullable
        @Override
        public String getDescription() {
            return super.getDescription();
        }

        @NotNull
        @Override
        public List<String> getDescriptions() {
            return super.getDescriptions();
        }
    }
}
