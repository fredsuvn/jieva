package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Provider to generate proxy instance.
 *
 * @author fredsuvn
 */
public interface ProxyProvider {

    /**
     * Generates a new proxy instance with specified class loader, upper classes and proxy method invoker supplier.
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
     * The proxy method invoker supplier provides the execution body of proxy method. Provider pass each public method
     * of upper classes (by {@link Class#getMethods()}) to the supplier, if supplier returns a non-null invoker, the
     * invoker will override the upper method. Otherwise (return {@code null}), the upper method will not be proxied.
     *
     * @param loader          specified class loader, may be {@code null}
     * @param uppers          upper classes of proxy type
     * @param invokerSupplier proxy method invoker supplier
     * @param <T>             proxied type
     * @return a new proxy instance
     */
    <T> T newProxyInstance(
        @Nullable ClassLoader loader,
        Iterable<Class<?>> uppers,
        Function<Method, @Nullable ProxyInvoker> invokerSupplier
    );
}
