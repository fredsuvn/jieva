package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.bean.BeanException;
import xyz.fslabo.common.bean.BeanResolvingException;
import xyz.fslabo.common.invoke.InvokingException;
import xyz.fslabo.common.reflect.proxy.ProxyException;

public class MiscTest {

    @Test
    public void testMisc() {

        Assert.expectThrows(InvokingException.class, () -> {
            throw new InvokingException();
        });
        Assert.expectThrows(InvokingException.class, () -> {
            throw new InvokingException("");
        });
        Assert.expectThrows(InvokingException.class, () -> {
            throw new InvokingException("", new RuntimeException());
        });

        Assert.expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException();
        });
        Assert.expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException(String.class);
        });
        Assert.expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException(new RuntimeException());
        });
        Assert.expectThrows(BeanException.class, () -> {
            throw new BeanException();
        });
        Assert.expectThrows(BeanException.class, () -> {
            throw new BeanException(new RuntimeException());
        });
        Assert.expectThrows(ProxyException.class, () -> {
            throw new ProxyException();
        });
        Assert.expectThrows(ProxyException.class, () -> {
            throw new ProxyException(new RuntimeException());
        });
        Assert.expectThrows(ProxyException.class, () -> {
            throw new ProxyException("", new RuntimeException());
        });
    }
}
