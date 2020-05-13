package xyz.srclab.common.bean;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
final class BeanWalkerSupport {

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
            propertyMap.forEach((name, property) -> {

            });
        }
    }

    private static final class BeanPathImpl implements BeanPath {

        @Override
        public boolean contains(Object owner, BeanProperty property) {
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
