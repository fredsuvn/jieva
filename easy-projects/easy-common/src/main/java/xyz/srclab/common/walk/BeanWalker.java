package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.bean.BeanClass;
import xyz.srclab.common.bean.BeanOperator;

/**
 * @author sunqian
 */
public class BeanWalker implements Walker {

    private final BeanOperator beanOperator;
    private final WalkerProvider walkerProvider;

    public BeanWalker(BeanOperator beanOperator, WalkerProvider walkerProvider) {
        this.beanOperator = beanOperator;
        this.walkerProvider = walkerProvider;
    }

    @Override
    public void walk(Object walked, WalkVisitor visitor) {
        BeanClass beanClass = beanOperator.resolveBean(walked.getClass());
        beanClass.getReadableProperties().forEach((name, property) -> {
            @Nullable Object value = property.getValue(walked);
            visitor.visit(name, value, walkerProvider);
        });
    }
}
