package xyz.srclab.common.bytecode.provider.spring;

import org.springframework.cglib.beans.BeanGenerator;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.bytecode.bean.BeanClass;

/**
 * @author sunqian
 */
final class BeanClassBuilderImpl<T>
        extends CacheStateBuilder<BeanClass<T>> implements BeanClass.Builder<T> {

    private final BeanGenerator beanGenerator;

    BeanClassBuilderImpl(Class<T> superClass) {
        this.beanGenerator = new BeanGenerator();
        this.beanGenerator.setSuperclass(superClass);
    }

    @Override
    public BeanClass.Builder<T> addProperty(String name, Class<?> type) {
        changeState();
        beanGenerator.addProperty(name, type);
        return this;
    }

    @Override
    protected BeanClass<T> buildNew() {
        return () -> (T) beanGenerator.create();
    }
}
