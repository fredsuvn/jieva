package xyz.srclab.sample.lang;

import org.testng.annotations.Test;

public class LangSampleTest {

    private final LangSample langSample = new LangSample();

    @Test
    public void showComputed() throws Exception {
        langSample.showComputed();
    }

    @Test
    public void showRef() {
        langSample.showRef();
    }

    @Test
    public void showTypeRef() {
        langSample.showTypeRef();
    }
}
