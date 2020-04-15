package xyz.srclab.common.reflect.invoke;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.provider.AbstractProviderManager;
import xyz.srclab.common.reflect.invoke.provider.reflected.ReflectedInvokerProvider;

@ThreadSafe
public class InvokerProviderManager extends AbstractProviderManager<InvokerProvider> {

    public static final InvokerProviderManager INSTANCE = new InvokerProviderManager();

    @Override
    protected InvokerProvider createDefaultProvider() {
        // MethodHandle is stupid.
        //return MethodHandleInvokerProvider.INSTANCE;
        return ReflectedInvokerProvider.INSTANCE;
    }
}
