package xyz.srclab.common.design.builder;

/**
 * @author sunqian
 */
public abstract class ProductCachingBuilder<T> extends BaseProductCachingBuilder<T> {

    public T build() {
        return buildCaching();
    }
}
