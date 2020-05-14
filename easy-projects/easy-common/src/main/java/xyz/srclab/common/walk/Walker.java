package xyz.srclab.common.walk;

import xyz.srclab.common.bean.BeanOperator;

/**
 * @author sunqian
 */
public interface Walker {

    Object ROOT_INDEX = new Object();

    Walker DEFAULT = WalkerSupport.getDefaultWalker();

    static Walker withBeanOperator(BeanOperator beanOperator) {
        return WalkerSupport.withBeanOperator(beanOperator);
    }

    static Walker withProvider(WalkerProvider walkerProvider) {
        return WalkerSupport.withProvider(walkerProvider);
    }

    void walk(Object walked, WalkVisitor visitor);
}
