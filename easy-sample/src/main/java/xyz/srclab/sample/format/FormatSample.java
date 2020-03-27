package xyz.srclab.sample.format;

import xyz.srclab.common.lang.format.FormatHelper;
import xyz.srclab.common.test.asserts.AssertHelper;

public class FormatSample {

    public void showFastFormat() {
        String message = FormatHelper.fastFormat("This is {} style!", "slf4j");
        AssertHelper.printAssert(message, "This is slf4j style!");
    }
}
