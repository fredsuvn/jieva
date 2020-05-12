package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface BeanPath {

    boolean contains(BeanProperty property, Object owner);

    String toString();

    int hashCode();

    boolean equals(Object other);
}
