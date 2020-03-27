package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class DefaultBeanConverter implements BeanConverter {

    public static DefaultBeanConverter getInstance() {
        return INSTANCE;
    }

    private static final DefaultBeanConverter INSTANCE = new DefaultBeanConverter();

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Type to) {
        return DefaultBeanConverterHandler.getInstance().convert(from, to, DefaultBeanOperator.getInstance());
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Type to, BeanOperator beanOperator) {
        return DefaultBeanConverterHandler.getInstance().convert(from, to, beanOperator);
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Class<T> to) {
        return DefaultBeanConverterHandler.getInstance().convert(from, to, DefaultBeanOperator.getInstance());
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Class<T> to, BeanOperator beanOperator) {
        return DefaultBeanConverterHandler.getInstance().convert(from, to, beanOperator);
    }
}
