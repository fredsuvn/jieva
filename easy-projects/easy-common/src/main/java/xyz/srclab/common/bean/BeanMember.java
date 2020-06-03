package xyz.srclab.common.bean;

import xyz.srclab.annotation.Hide;

/**
 * @author sunqian
 */
@Hide
public interface BeanMember {

    String name();

    @Override
    int hashCode();

    @Override
    boolean equals(Object other);

    @Override
    String toString();
}
