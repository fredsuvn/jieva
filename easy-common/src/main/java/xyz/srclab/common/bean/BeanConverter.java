package xyz.srclab.common.bean;

public interface BeanConverter {

    static BeanConverterBuilder newBuilder() {
        return BeanConverterBuilder.newBuilder();
    }

    <T> T convert(Object from, Class<T> to);
}
