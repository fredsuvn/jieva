package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;

public class BDefaultTest {

    @Test
    public void testDefault() {
        System.out.println("Default radix: " + BDefault.radix());
        System.out.println("Default charset: " + BDefault.charset());
        System.out.println("Default bufferSize: " + BDefault.bufferSize());
        System.out.println("Default locale: " + BDefault.locale());
        System.out.println("Default serialVersion: " + BDefault.serialVersion());
        System.out.println("Default timestampPattern: " + BDefault.timestampPattern().getPattern());
    }
}
