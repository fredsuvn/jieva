package xyz.srclab.common.walk;

import xyz.srclab.common.bean.BeanOperator;

/**
 * @author sunqian
 */
final class WalkerSupport {

    static Walker getDefaultWalker() {
        return WalkerImpl.INSTANCE;
    }

    static Walker withBeanOperator(BeanOperator beanOperator) {
        if (beanOperator == BeanOperator.DEFAULT) {
            return getDefaultWalker();
        }
        return new WalkerImpl(new BeanOperatorWalkProvider(beanOperator));
    }

    static Walker withProvider(WalkerProvider walkerProvider) {
        if (walkerProvider == WalkerProvider.DEFAULT) {
            return getDefaultWalker();
        }
        return new WalkerImpl(walkerProvider);
    }

    private static final class WalkerImpl implements Walker {

        private static final WalkerImpl INSTANCE = new WalkerImpl(WalkerProvider.DEFAULT);

        private final WalkerProvider walkerProvider;

        private WalkerImpl(WalkerProvider walkerProvider) {
            this.walkerProvider = walkerProvider;
        }

        @Override
        public void walk(Object walked, WalkVisitor visitor) {
            Walker walker = walkerProvider.getWalker(walked);
            walker.walk(walked, visitor);
        }
    }
}
