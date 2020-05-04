package xyz.srclab.common.bytecode.provider.invoke.jdkasm;

import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.InvokerProvider;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public class AsmInvokerProvider implements InvokerProvider {

    private final Cache<String, ConstructorInvoker<?>> constructorInvokerCache = Cache.newGcThreadLocalL2();
    private final Cache<String, MethodInvoker> methodInvokerCache = Cache.newGcThreadLocalL2();
    private final Cache<String, FunctionInvoker> functionInvokerCache = Cache.newGcThreadLocalL2();

    private final AsmInvokerGenerator invokerGenerator = new AsmInvokerGenerator();

    @Override
    public <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor) {
        return null;
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method) {
        return null;
    }

    @Override
    public FunctionInvoker getFunctionInvoker(Method method) {
        return null;
    }
}
