package xyz.srclab.common.bean;

import xyz.srclab.common.ToovaBoot;

import java.util.Map;

/**
 * @author sunqian
 */
final class BeanSupport {

    private static final BeanProvider beanProvider = ToovaBoot.getProvider(BeanProvider.class);

    static BeanOperator getBeanOperator() {
        return beanProvider.getBeanOperator();
    }

    static BeanConverter getBeanConverter() {
        return beanProvider.getBeanConverter();
    }

    static BeanConverterHandler getBeanConverterHandler() {
        return beanProvider.getBeanConverterHandler();
    }

    static BeanResolver getBeanResolver() {
        return beanProvider.getBeanResolver();
    }

    static BeanResolverHandler getBeanResolverHandler() {
        return beanProvider.getBeanResolverHandler();
    }

    static BeanWalker newBeanWalker(Object bean, BeanOperator beanOperator) {
        return new BeanWalkerImpl(bean, beanOperator);
    }

    static BeanWalker newBeanWalker(Object bean, BeanClass beanClass, BeanOperator beanOperator) {
        return new BeanWalkerImpl(bean, beanClass, beanOperator);
    }

    private static final class BeanWalkerImpl implements BeanWalker {

        private final Object bean;
        private final BeanClass beanClass;
        private final BeanOperator beanOperator;

        private BeanWalkerImpl(Object bean, BeanOperator beanOperator) {
            this(bean, beanOperator.resolveBean(bean.getClass()), beanOperator);
        }

        private BeanWalkerImpl(Object bean, BeanClass beanClass, BeanOperator beanOperator) {
            this.bean = bean;
            this.beanClass = beanClass;
            this.beanOperator = beanOperator;
        }

        @Override
        public void walk(BeanVisitor visitor) {
            Map<String, BeanProperty> propertyMap = beanClass.getReadableProperties();
            propertyMap.forEach((name, property) -> {
                visitor.visit(bean, property, beanOperator, this);
            });
        }
    }
}
