package xyz.srclab.common.bean;

import xyz.srclab.common.ToovaBoot;

import java.util.LinkedList;
import java.util.List;
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

        private final BeanOperator beanOperator;
        private final Object bean;

        private BeanWalkerImpl(BeanOperator beanOperator, Object bean) {
            this.beanOperator = beanOperator;
            this.bean = bean;
        }

        @Override
        public void walk(BeanVisitor visitor) {
            BeanClass beanClass = beanOperator.resolveBean(bean.getClass());
            Map<String, BeanProperty> propertyMap = beanClass.getReadableProperties();
            List<Object> visitPath = new LinkedList<>();
            propertyMap.forEach((name, property)->{

            });
        }
    }

    private static final class BeanPathImpl implements BeanPath {



        @Override
        public boolean contains(BeanProperty property, Object owner) {
            return false;
        }

        @Override
        public String toString() {
            return "BeanPathImpl{}";
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
