package xyz.srclab.common.bean;

import java.util.List;

/**
 * @author sunqian
 */
public interface BeanVisitor {

    void visit(Object object, BeanProperty property, List<Object> visitPath);
}
