package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

/**
 * @author sunqian
 */
@Immutable
public interface BeanWalker {

    void walk(BeanVisitor visitor);
}
