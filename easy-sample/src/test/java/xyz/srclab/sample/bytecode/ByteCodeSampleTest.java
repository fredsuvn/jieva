package xyz.srclab.sample.bytecode;

import org.testng.annotations.Test;

public class ByteCodeSampleTest {

    private final ByteCodeSample byteCodeSample = new ByteCodeSample();

    @Test
    public void showProxy() {
        byteCodeSample.showProxy();
    }

    @Test
    public void showBean() throws Exception {
        byteCodeSample.showBean();
    }
}
