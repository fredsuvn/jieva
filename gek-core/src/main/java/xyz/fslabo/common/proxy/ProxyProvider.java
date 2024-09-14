package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Provider to generate proxy instance. There are two implementation: {@link JdkProxyProvider} and
 * {@link AsmProxyProvider}.
 *
 * @author fredsuvn
 * @see JdkProxyProvider
 * @see AsmProxyProvider
 */
public interface ProxyProvider {

    /**
     * Generates a new proxy instance with specified class loader, proxied class and interfaces and proxy method
     * handler.
     * <p>
     * The specified class loader may be {@code null}, in this case the provider will use implementation default class
     * loader. The proxied class and interfaces indicates which type the proxy type {@code extends} and
     * {@code implements}, in order of clause of the declaration. For example, let the proxy type declare as:
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
     * @param loader  specified class loader, may be {@code null}
     * @param proxied proxied class and interfaces
     * @param handler proxy method handler
     * @param <T>     proxied type
     * @return a new proxy instance
     */
    <T> T newProxyInstance(
        @Nullable ClassLoader loader,
        Iterable<Class<?>> proxied,
        MethodProxyHandler handler
    );
}
