package xyz.srclab.common.bean;

import org.apache.commons.beanutils.ConvertUtilsBean;

public class CommonBeanConverter implements BeanConverter {

    public static CommonBeanConverter getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanConverter INSTANCE = new CommonBeanConverter();

    private final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

    @Override
    public <T> T convert(Object from, Class<T> to) {
        return (T) convertUtilsBean.convert(from, to);
    }
}
