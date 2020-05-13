package xyz.srclab.common.bean;

/**
 * @author sunqian
 */
public interface BeanMember {

    String getName();

    @Override
    boolean equals(Object other);

    @Override
    int hashCode();
}
