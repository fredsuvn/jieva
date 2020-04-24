package xyz.srclab.common.bytecode.provider.cglib;

import net.sf.cglib.beans.BeanGenerator;
import xyz.srclab.common.bytecode.BeanClass;
import xyz.srclab.common.util.pattern.builder.CachedBuilder;

/**
 * @author sunqian
 */
final class BeanClassBuilderImpl<T>
        extends CachedBuilder<BeanClass<T>> implements BeanClass.Builder<T> {

    private final BeanGenerator beanGenerator;

    BeanClassBuilderImpl(Class<T> superClass) {
        this.beanGenerator = new BeanGenerator();
        this.beanGenerator.setSuperclass(superClass);
    }

    @Override
    public BeanClass.Builder<T> addProperty(String name, Class<?> type) {
        updateState();
        addProperty(name, type);
        return this;
    }

    @Override
    protected BeanClass<T> buildNew() {
        return () -> (T) beanGenerator.create();
    }
}
