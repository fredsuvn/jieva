package xyz.srclab.sample.format;

import xyz.srclab.common.string.format.fastformat.FastFormat;
import xyz.srclab.test.asserts.AssertHelper;

public class FormatSample {

    public void showFastFormat() {
        String message = FastFormat.format("This is {} style!", "slf4j");
        AssertHelper.printAssert(message, "This is slf4j style!");
    }
}
