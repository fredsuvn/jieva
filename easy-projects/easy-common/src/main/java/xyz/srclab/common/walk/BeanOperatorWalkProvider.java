package xyz.srclab.common.walk;

import xyz.srclab.common.bean.BeanOperator;

import java.util.Map;

/**
 * @author sunqian
 */
class BeanOperatorWalkProvider implements WalkerProvider {

    private final BeanWalker beanWalker;
    private final ArrayWalker arrayWalker = new ArrayWalker(this);
    private final IterableWalker iterableWalker = new IterableWalker(this);
    private final MapWalker mapWalker = new MapWalker(this);

    BeanOperatorWalkProvider(BeanOperator beanOperator) {
        this.beanWalker = new BeanWalker(beanOperator, this);
    }

    @Override
    public Walker getWalker(Object walked) {
        Class<?> type = walked.getClass();
        if (type.isArray()) {
            return arrayWalker;
        }
        if (Iterable.class.isAssignableFrom(type)) {
            return iterableWalker;
        }
        if (Map.class.isAssignableFrom(type)) {
            return mapWalker;
        }
        return beanWalker;
    }
}
