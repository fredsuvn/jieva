package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

/**
 * @author sunqian
 */
@Immutable
public interface BeanVisitor {

    boolean needDeepVisit(Object object, BeanProperty property, BeanPath path);

    void visitProperty(Object object, BeanProperty property, BeanPath path);
}
