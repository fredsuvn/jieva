package xyz.srclab.sample.proxy;

import org.testng.annotations.Test;

public class ProxySampleTest {

    private final ProxySample proxySample = new ProxySample();

    @Test
    public void showProxy() {
        proxySample.showProxy();
    }
}
