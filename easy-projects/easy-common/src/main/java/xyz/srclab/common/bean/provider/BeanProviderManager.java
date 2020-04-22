package xyz.srclab.common.bean.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.pattern.provider.AbstractProviderManager;

/**
 * @author sunqian
 */
@ThreadSafe
public class BeanProviderManager extends AbstractProviderManager<BeanProvider> {

    public static BeanProviderManager INSTANCE = new BeanProviderManager();

    {

    }
}
