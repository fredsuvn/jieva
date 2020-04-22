package xyz.srclab.common.invoke.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.pattern.provider.AbstractProviderManager;
import xyz.srclab.common.invoke.provider.bytecode.ByteCodeInvokerProvider;
import xyz.srclab.common.invoke.provider.reflected.ReflectedInvokerProvider;

@ThreadSafe
public class InvokerProviderManager extends AbstractProviderManager<InvokerProvider> {

    public static final InvokerProviderManager INSTANCE = new InvokerProviderManager();

    @Override
    protected InvokerProvider createDefaultProvider() {
        // MethodHandle is stupid.
        //return MethodHandleInvokerProvider.INSTANCE;

        try {
            return ByteCodeInvokerProvider.INSTANCE;
        } catch (Exception e) {
            return ReflectedInvokerProvider.INSTANCE;
        }
    }
}
