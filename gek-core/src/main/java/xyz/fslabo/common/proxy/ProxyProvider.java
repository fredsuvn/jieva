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
     * Generates a new proxy instance with specified class loader, supper class, interfaces and proxy method invoker
     * supplier.
     * <p>
     * The specified class loader may be {@code null}, in this case the provider will use implementation default class
     * loader. The super class and interfaces indicates which type the proxy type {@code extends} and
     * {@code implements}, in order of clause of the declaration. For example, let the proxy type declare as:
     * <pre>
     *     public class ProxyType extends SuperClass implements SuperInterface
     * </pre>
     * The super class should be {@code SuperClass.class} and the interfaces should be {@code SuperInterface.class}.
     * Super class can be {@code null} if its super class is {@link Object}, and interfaces also can be empty. Either
     * the superclass or the parent interface can be {@code null} or empty, but not both.
     * <p>
     * The proxy method invoker supplier provides the execution body of proxy method. Provider pass each public method
     * of upper types to the supplier, if supplier returns a non-null invoker, the invoker will override the upper
     * method. Otherwise, the upper method will not be proxied.
     *
     * @param loader          specified class loader, may be {@code null}
     * @param superClass      super class of proxy type
     * @param interfaces      interfaces of proxy type
     * @param invokerSupplier proxy method invoker supplier
     * @param <T>             proxied type
     * @return a new proxy instance
     */
    <T> T newProxyInstance(
        @Nullable ClassLoader loader,
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> interfaces,
        Function<Method, @Nullable ProxyInvoker> invokerSupplier
    );
}
