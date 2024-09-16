package xyz.fslabo.common.reflect.proxy;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Utilities for class proxy.
 *
 * @author fredsuvn
 */
public class JieProxy {

    /**
     * Generates a new proxy instance with specified proxied class and interfaces and proxy method handler by
     * {@link AsmProxyProvider#newProxyInstance(ClassLoader, Iterable, MethodProxyHandler)}.
     * <p>
     * The proxied class and interfaces indicates which type the proxy type {@code extends} and {@code implements}, in
     * order of clause of the declaration. For example, let the proxy type declare as:
     * <pre>
     *     public class ProxyType extends SuperClass implements SuperInterface
     * </pre>
     * The proxied class and interfaces should be:
     * <pre>
     *     SuperClass.class, SuperInterface.class
     * </pre>
     * The proxy method handler provides the execution body of proxy method. Provider passes each non-final and
     * non-static methods of proxied class and interfaces (by {@link Class#getMethods()}) to
     * {@link MethodProxyHandler#proxy(Method)}, if returns {@code true}, the provider will override the method on
     * generated instance.
     * <p>
     * If there are methods with the same name and signature from different declared classes,
     * {@link ProxyInvoker#invokeSuper(Object[])} will typically invoke the first one.
     *
     * @param proxied proxied class and interfaces
     * @param handler proxy method handler
     * @param <T>     proxied type
     * @return a new proxy instance
     * @see ProxyProvider
     * @see AsmProxyProvider
     */
    public static <T> T asm(Iterable<Class<?>> proxied, MethodProxyHandler handler) {
        return AsmProxy.proxy(proxied, handler);
    }

    /**
     * Generates a new proxy instance with specified class loader, proxied interfaces and proxy method handler by
     * {@link JdkProxyProvider#newProxyInstance(ClassLoader, Iterable, MethodProxyHandler)}.
     * <p>
     * The interfaces param indicates which type the proxy type {@code implements}, in order of clause of the
     * declaration. For example, let the proxy type declare as:
     * <pre>
     *     public class ProxyType implements SuperInterface1, SuperInterface2
     * </pre>
     * The interfaces param should be:
     * <pre>
     *     SuperInterface1.class, SuperInterface2.class
     * </pre>
     * The proxy method handler provides the execution body of proxy method. Provider passes each non-final and
     * non-static methods of proxied interfaces (by {@link Class#getMethods()}) to
     * {@link MethodProxyHandler#proxy(Method)}, if returns {@code true}, the provider will override the method on
     * generated instance.
     * <p>
     * Note this method only can proxy interfaces, try {@link #asm(Iterable, MethodProxyHandler)} to {@code extends}
     * super class.
     *
     * @param loader  specified class loader
     * @param proxied proxied interfaces
     * @param handler proxy method handler
     * @param <T>     proxied type
     * @return a new proxy instance
     * @see ProxyProvider
     * @see JdkProxyProvider
     */
    public static <T> T jdk(@Nullable ClassLoader loader, Iterable<Class<?>> proxied, MethodProxyHandler handler) {
        return JdkProxy.proxy(loader, proxied, handler);
    }


    private static final class AsmProxy {

        private static final ProxyProvider ASM = new AsmProxyProvider();

        private static <T> T proxy(Iterable<Class<?>> proxied, MethodProxyHandler handler) {
            return ASM.newProxyInstance(null, proxied, handler);
        }
    }

    private static final class JdkProxy {

        private static final ProxyProvider JDK = new JdkProxyProvider();

        private static <T> T proxy(@Nullable ClassLoader loader, Iterable<Class<?>> proxied, MethodProxyHandler handler) {
            return JDK.newProxyInstance(loader, proxied, handler);
        }
    }
}
