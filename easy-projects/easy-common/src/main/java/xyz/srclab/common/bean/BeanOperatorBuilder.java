package xyz.srclab.common.bean;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.object.Converter;
import xyz.srclab.common.pattern.builder.CachedBuilder;

/**
 * @author sunqian
 */
public class BeanOperatorBuilder extends CachedBuilder<BeanOperator> {

    private @Nullable BeanResolver beanResolver;
    private @Nullable Converter converter;

    public BeanOperatorBuilder resolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
        this.updateState();
        return this;
    }

    public BeanOperatorBuilder converter(Converter converter) {
        this.converter = converter;
        this.updateState();
        return this;
    }

    @Override
    protected BeanOperator buildNew() {
        return new BeanOperatorImpl(this);
    }

    private static final class BeanOperatorImpl implements BeanOperator {

        private final BeanResolver beanResolver;
        private final Converter converter;

        private BeanOperatorImpl(BeanOperatorBuilder builder) {
            this.beanResolver = builder.beanResolver == null ?
                    BeanResolver.getDefault() : builder.beanResolver;
            this.converter = builder.converter == null ?
                    Converter.getDefault() : builder.converter;
        }

        @Override
        public BeanResolver resolver() {
            return beanResolver;
        }

        @Override
        public Converter converter() {
            return converter;
        }
    }
}
