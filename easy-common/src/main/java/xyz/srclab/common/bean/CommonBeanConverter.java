package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class CommonBeanConverter implements BeanConverter {

    public static CommonBeanConverter getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanConverter INSTANCE = new CommonBeanConverter();

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Type to) {
        return CommonBeanConverterHandler.getInstance().convert(from, to, CommonBeanOperator.getInstance());
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Type to, BeanOperator beanOperator) {
        return CommonBeanConverterHandler.getInstance().convert(from, to, beanOperator);
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Class<T> to) {
        return CommonBeanConverterHandler.getInstance().convert(from, to, CommonBeanOperator.getInstance());
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object from, Class<T> to, BeanOperator beanOperator) {
        return CommonBeanConverterHandler.getInstance().convert(from, to, beanOperator);
    }
}
