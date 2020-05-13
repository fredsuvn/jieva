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

    static BeanWalker newBeanWalker(BeanOperator beanOperator, Object bean) {
        return new BeanWalkerImpl(beanOperator, bean);
    }

    private static final class BeanWalkerImpl implements BeanWalker {

        private final BeanClass beanClass;
        private final BeanOperator beanOperator;
        private final Object bean;

        private BeanWalkerImpl(BeanOperator beanOperator, Object bean) {
            this(beanOperator.resolveBean(bean.getClass()), beanOperator, bean);
        }

        private BeanWalkerImpl(BeanClass beanClass, BeanOperator beanOperator, Object bean) {
            this.beanClass = beanClass;
            this.beanOperator = beanOperator;
            this.bean = bean;
        }

        @Override
        public void walk(BeanVisitor visitor) {
            Map<String, BeanProperty> propertyMap = beanClass.getReadableProperties();
            propertyMap.forEach((name, property) -> {
                //visitor.visit();
            });
        }
    }
}
