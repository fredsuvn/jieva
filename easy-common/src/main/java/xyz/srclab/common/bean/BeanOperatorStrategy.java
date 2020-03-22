package xyz.srclab.common.bean;

import xyz.srclab.common.builder.CacheStateBuilder;

public interface BeanOperatorStrategy {

    static Builder newBuilder() {
        return Builder.newBuilder();
    }

    CopyProperty getCopyPropertyStrategy();

    interface CopyProperty {

        void copyProperty(
                BeanPropertyDescriptor sourceProperty, Object sourceBean,
                BeanPropertyDescriptor destProperty, Object destBean,
                BeanOperator beanOperator);
    }

    class Builder extends CacheStateBuilder<BeanOperatorStrategy> {

        public static Builder newBuilder() {
            return new Builder();
        }

        private CopyProperty copyProperty;

        public Builder setCopyProperty(CopyProperty copyProperty) {
            this.changeState();
            this.copyProperty = copyProperty;
            return this;
        }

        @Override
        protected BeanOperatorStrategy buildNew() {
            return new BeanOperatorStrategyImpl(this);
        }

        private static class BeanOperatorStrategyImpl implements BeanOperatorStrategy {

            private final CopyProperty copyProperty;

            private BeanOperatorStrategyImpl(Builder builder) {
                this.copyProperty = builder.copyProperty == null ?
                        CommonCopyPropertyStrategy.getInstance() : builder.copyProperty;
            }

            @Override
            public CopyProperty getCopyPropertyStrategy() {
                return copyProperty;
            }
        }
    }
}
