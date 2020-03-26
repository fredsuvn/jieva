package xyz.srclab.sample.bean;

import org.testng.annotations.Test;

public class BeanSampleTest {

    private final BeanSample beanSample = new BeanSample();

    @Test
    public void showConvert() throws Exception {
        beanSample.showConvert();
    }

    @Test
    public void showCopyProperties() throws Exception {
        beanSample.showCopyProperties();
    }

    @Test
    public void showPopulateProperties() {
        beanSample.showPopulateProperties();
    }

    @Test
    public void showCustom() {
        beanSample.showCustom();
    }
}
