package xyz.srclab.common.util.proxy;

import xyz.srclab.common.design.builder.CachedBuilder;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractProxyClassBuilder<T>
        extends CachedBuilder<ProxyClass<T>> implements ProxyClassBuilder<T> {

    protected final Class<T> type;
    protected final Map<Predicate<Method>, ProxyMethod> proxyMethodMap = new LinkedHashMap<>();

    protected AbstractProxyClassBuilder(Class<T> type) {
        this.type = type;
    }

    @Override
    public ProxyClassBuilder<T> proxyMethod(Predicate<Method> methodPredicate, ProxyMethod proxyMethod) {
        proxyMethodMap.put(methodPredicate, proxyMethod);
        this.updateState();
        return this;
    }
}
