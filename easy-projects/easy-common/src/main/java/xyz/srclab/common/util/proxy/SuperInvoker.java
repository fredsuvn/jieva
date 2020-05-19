package xyz.srclab.common.util.proxy;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface SuperInvoker {

    @Nullable
    Object invoke(Object object, Object... args);
}
