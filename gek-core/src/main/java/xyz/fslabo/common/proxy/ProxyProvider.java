package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Provider to generate proxy instance.
 *
 * @author fredsuvn
 */
public interface ProxyProvider {

    /**
     * Generates a new proxy instance with specified class loader, upper classes and proxy method handler.
     * <p>
     * The specified class loader may be {@code null}, in this case the provider will use implementation default class
     * loader. The upper classes indicates which type the proxy type {@code extends} and {@code implements}, in order of
     * clause of the declaration. For example, let the proxy type declare as:
     * <pre>
     *     public class ProxyType extends SuperClass implements SuperInterface
     * </pre>
     * The upper classes should be:
     * <pre>
     *     SuperClass.class, SuperInterface.class
     * </pre>
     * The proxy method handler provides the execution body of proxy method. Provider passes each non-final and
     * non-static methods of upper classes (by {@link Class#getMethods()}) to {@link MethodProxyHandler#proxy(Method)},
     * if returns {@code true}, the provider will override the method on generated instance.
     *
     * @param loader  specified class loader, may be {@code null}
     * @param uppers  upper classes of proxy type
     * @param handler proxy method handler
     * @param <T>     proxied type
     * @return a new proxy instance
     */
    <T> T newProxyInstance(
        @Nullable ClassLoader loader,
        Iterable<Class<?>> uppers,
        MethodProxyHandler handler
    );
}
