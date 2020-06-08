package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.bean.BeanClass;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.bean.BeanProperty;

import java.util.Map;
import java.util.Set;

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
        Set<Map.Entry<String, BeanProperty>> set = beanClass.readableProperties().entrySet();
        loop:
        for (Map.Entry<String, BeanProperty> entry : set) {
            String name = entry.getKey();
            BeanProperty property = entry.getValue();
            @Nullable Object value = property.getValue(walked);
            WalkVisitResult result = visitor.visit(name, value, walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }
}
