package xyz.srclab.common.bean;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.format.FormatHelper;
import xyz.srclab.common.reflect.ReflectHelper;

public class CommonBeanConverterHandler implements BeanConverterHandler {

    public static CommonBeanConverterHandler getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanConverterHandler INSTANCE = new CommonBeanConverterHandler();

    private final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
    private final BeanOperator beanOperator = CommonBeanOperator.getInstance();

    @Override
    public boolean supportConvert(Object from, Class<?> to) {
        return true;
    }

    @Override
    @Nullable
    public <T> T convert(@Nullable Object from, Class<T> to) {
        if (from == null) {
            return (T) convertByConvertUtilsBean(from, to);
        }

        Object result = convertByConvertUtilsBean(from, to);
        if (result == null) {
            return null;
        }
        if (ReflectHelper.isAssignable(result, to)) {
            return (T) result;
        }
        result = convertByBeanOperator(from, to);
        if (ReflectHelper.isAssignable(result, to)) {
            return (T) result;
        }
        throw new UnsupportedOperationException(
                FormatHelper.fastFormat("Cannot convert object {} to type {}", from, to)
        );
    }

    @Nullable
    private Object convertByConvertUtilsBean(@Nullable Object from, Class<?> to) {
        return convertUtilsBean.convert(from, to);
    }

    private Object convertByBeanOperator(Object from, Class<?> to) {
        Object toInstance = ReflectHelper.newInstance(to);
        beanOperator.copyProperties(from, toInstance);
        return toInstance;
    }
}
