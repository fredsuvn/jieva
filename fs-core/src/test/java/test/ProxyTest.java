package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.proxy.FsProxy;

import java.lang.reflect.Method;
import java.util.Objects;

public class ProxyTest {

    @Test
    public void testJdkProxy() {
        FsProxy<Foo> fooProxy = FsProxy.newBuilder()
            .sourceClass(Foo.class)
            .proxyMethod("foo", new Class[]{String.class},
                (args, sourceMethod, sourceInvocation) -> "Fuck All!"
            )
            .build();
        Assert.assertEquals(fooProxy.newInstance().foo("fuck!"), "Fuck All!");
    }

    public interface Foo {

        String foo(String input);
    }
}
