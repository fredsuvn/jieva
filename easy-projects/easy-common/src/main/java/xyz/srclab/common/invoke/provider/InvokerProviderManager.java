package xyz.srclab.common.invoke.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.EasyBoot;
import xyz.srclab.common.util.pattern.provider.LoadingProviderManager;

@ThreadSafe
public class InvokerProviderManager extends LoadingProviderManager<InvokerProvider> {

    public static final InvokerProviderManager INSTANCE = new InvokerProviderManager();

    public InvokerProviderManager() {
        super(EasyBoot.getProviderProperties().get(InvokerProvider.class.getName()));
    }
}
