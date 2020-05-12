package xyz.srclab.common.bean;

/**
 * @author sunqian
 */
public interface BeanWalker {

    void walk(BeanVisitor visitor);
}
