package xyz.srclab.common.bean;

public interface BeanConverterHandler {

    boolean supportConvert(Object from, Class<?> to);

    <T> T convert(Object from, Class<T> to);
}
