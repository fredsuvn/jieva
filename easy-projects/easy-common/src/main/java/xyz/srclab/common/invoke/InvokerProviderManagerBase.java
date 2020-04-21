package xyz.srclab.common.invoke;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.pattern.provider.AbstractProviderManager;
import xyz.srclab.common.invoke.provider.bytecode.ByteCodeInvokerProvider;
import xyz.srclab.common.invoke.provider.reflected.ReflectedInvokerProvider;

@ThreadSafe
public class InvokerProviderManagerBase extends AbstractProviderManager<InvokerProvider> {

    public static final InvokerProviderManagerBase INSTANCE = new InvokerProviderManagerBase();

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
