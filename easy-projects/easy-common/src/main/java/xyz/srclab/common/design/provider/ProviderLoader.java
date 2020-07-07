package xyz.srclab.common.design.provider;

/**
 * @author sunqian
 */
public interface ProviderLoader<T> {

    ProviderPool<T> load();
}
