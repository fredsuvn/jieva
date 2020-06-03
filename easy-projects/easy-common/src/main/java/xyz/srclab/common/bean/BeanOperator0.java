package xyz.srclab.common.bean;

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

    private static final class DefaultOperatorHolder {

        private static final BeanOperator SINGLETON = newOperatorBuilder().build();
    }
}
