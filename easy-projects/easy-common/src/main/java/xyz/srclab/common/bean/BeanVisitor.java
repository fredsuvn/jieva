package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

/**
 * @author sunqian
 */
@Immutable
public interface BeanVisitor {

    void visit(Object owner, BeanProperty beanProperty, BeanOperator beanOperator, BeanWalker beanWalker);
}
