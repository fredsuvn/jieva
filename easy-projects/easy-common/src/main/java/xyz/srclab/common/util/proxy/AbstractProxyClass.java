package xyz.srclab.common.util.proxy;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.MapHelper;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

@Immutable
public abstract class AbstractProxyClass<T> implements ProxyClass<T> {

    protected final Class<?> type;
    protected final Map<Method, ProxyMethod> methodMap;

    protected AbstractProxyClass(Class<?> type, Map<Predicate<Method>, ProxyMethod> proxyMethodMap) {
        this.type = type;
        if (proxyMethodMap.isEmpty()) {
            this.methodMap = Collections.emptyMap();
            return;
        }
        Map<Method, ProxyMethod> methodMap = new LinkedHashMap<>();
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            proxyMethodMap.forEach((predicate, proxyMethod) -> {
                if (predicate.test(method)) {
                    methodMap.put(method, proxyMethod);
                }
            });
        }
        this.methodMap = MapHelper.immutable(methodMap);
    }
}
