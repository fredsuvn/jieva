package test;

import org.testng.annotations.Test;
import xyz.fslabo.common.bean.BeanException;
import xyz.fslabo.common.bean.BeanResolvingException;
import xyz.fslabo.common.invoke.InvocationException;
import xyz.fslabo.common.reflect.proxy.ProxyException;

import static org.testng.Assert.expectThrows;

public class MiscTest {

    @Test
    public void testMisc() {

        expectThrows(InvocationException.class, () -> {
            throw new InvocationException();
        });
        expectThrows(InvocationException.class, () -> {
            throw new InvocationException("");
        });
        expectThrows(InvocationException.class, () -> {
            throw new InvocationException("", new RuntimeException());
        });

        expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException();
        });
        expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException(String.class);
        });
        expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException(new RuntimeException());
        });
        expectThrows(BeanException.class, () -> {
            throw new BeanException();
        });
        expectThrows(BeanException.class, () -> {
            throw new BeanException(new RuntimeException());
        });
        expectThrows(ProxyException.class, () -> {
            throw new ProxyException();
        });
        expectThrows(ProxyException.class, () -> {
            throw new ProxyException(new RuntimeException());
        });
        expectThrows(ProxyException.class, () -> {
            throw new ProxyException("", new RuntimeException());
        });
    }
}
