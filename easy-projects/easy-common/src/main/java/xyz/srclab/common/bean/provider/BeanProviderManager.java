package xyz.srclab.common.bean.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.EasyBoot;
import xyz.srclab.common.pattern.provider.LoadingProviderManager;

/**
 * @author sunqian
 */
@ThreadSafe
public class BeanProviderManager extends LoadingProviderManager<BeanProvider> {

    public static BeanProviderManager INSTANCE = new BeanProviderManager();

    public BeanProviderManager() {
        super(EasyBoot.getProviderProperties().get(BeanProvider.class.getName()));
    }
}
