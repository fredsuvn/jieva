package xyz.srclab.common.bytecode.impl.cglib;

public interface BeanGenerator {

    void setSuperclass(Class<?> superclass);

    void addProperty(String name, Class<?> type);

    Object create();
}
