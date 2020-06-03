package xyz.srclab.common.bean;

import java.util.Map;

/**
 * @author sunqian
 */
final class BeanOperator0 {

    static BeanOperator getDefault() {
        return DefaultOperatorHolder.SINGLETON;
    }

    static BeanOperatorBuilder newOperatorBuilder() {
        return new BeanOperatorBuilder();
    }

    static void copyProperties(Object from, Object to, BeanOperator operator) {


    }

    static void copyPropertiesIgnoreNull(Object from, Object to, BeanOperator operator) {

    }

    private static void mapToMap(Map<?, ?> from, Map<?, ?> to) {

    }

    private static final class DefaultOperatorHolder {

        private static final BeanOperator SINGLETON = newOperatorBuilder().build();
    }
}
