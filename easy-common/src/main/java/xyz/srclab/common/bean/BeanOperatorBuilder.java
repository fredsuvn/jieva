package xyz.srclab.common.bean;

public class BeanOperatorBuilder {

    public static BeanOperatorBuilder newBuilder() {
        return new BeanOperatorBuilder();
    }

    private BeanResolver beanResolver = CommonBeanResolver.getInstance();
    private BeanConverter beanConverter = CommonBeanConverter.getInstance();
    private BeanOperatorStrategy.CopyProperty copyPropertyStrategy =
            CommonBeanOperatorStrategy.CopyProperty.getInstance();

    public BeanOperatorBuilder setBeanResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
        return this;
    }

    public BeanOperatorBuilder setBeanConverter(BeanConverter beanConverter) {
        this.beanConverter = beanConverter;
        return this;
    }

    public BeanOperatorBuilder setCopyPropertyStrategy(BeanOperatorStrategy.CopyProperty copyPropertyStrategy) {
        this.copyPropertyStrategy = copyPropertyStrategy;
        return this;
    }

    public BeanOperator build() {
        return new BeanOperatorImpl(this);
    }

    private static class BeanOperatorImpl implements BeanOperator {

        private final BeanResolver beanResolver;
        private final BeanConverter beanConverter;
        private final BeanOperatorStrategy.CopyProperty copyPropertyStrategy;

        private BeanOperatorImpl(BeanOperatorBuilder builder) {
            this.beanResolver = builder.beanResolver;
            this.beanConverter = builder.beanConverter;
            this.copyPropertyStrategy = builder.copyPropertyStrategy;
        }

        @Override
        public BeanResolver getBeanResolver() {
            return beanResolver;
        }

        @Override
        public BeanConverter getBeanConverter() {
            return beanConverter;
        }

        @Override
        public BeanOperatorStrategy.CopyProperty getCopyPropertyStrategy() {
            return copyPropertyStrategy;
        }
    }
}
