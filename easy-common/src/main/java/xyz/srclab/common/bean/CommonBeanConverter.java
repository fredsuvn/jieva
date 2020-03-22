package xyz.srclab.common.bean;

import org.jetbrains.annotations.Nullable;

public class CommonBeanConverter implements BeanConverter {

    public static CommonBeanConverter getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanConverter INSTANCE = new CommonBeanConverter();

    @Override
    @Nullable
    public <T> T convert(@Nullable Object from, Class<T> to) {
        return CommonBeanConverterHandler.getInstance().convert(from, to);
    }
}
