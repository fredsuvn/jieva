package xyz.srclab.sample.reflect;

import org.testng.annotations.Test;

public class ReflectSampleTest {

    private final ReflectSample reflectSample = new ReflectSample();

    @Test
    public void showReflect() {
        reflectSample.showReflect();
    }
}
