package xyz.srclab.bytecode.provider.cglib;

public interface BeanGenerator {

    void setSuperclass(Class<?> superclass);

    void addProperty(String name, Class<?> type);

    Object create();
}
