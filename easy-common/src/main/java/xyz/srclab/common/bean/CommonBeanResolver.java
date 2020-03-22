package xyz.srclab.common.bean;

import java.util.Map;

public class CommonBeanResolver implements BeanResolver {

    public static CommonBeanResolver getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanResolver INSTANCE = new CommonBeanResolver();

    @Override
    public BeanDescriptor resolve(Object bean) {
        return bean instanceof Map ?
                MapBeanResolverHandler.getInstance().resolve(bean, CommonBeanOperator.getInstance())
                :
                SimpleBeanResolverHandler.getInstance().resolve(bean, CommonBeanOperator.getInstance());
    }

    @Override
    public BeanDescriptor resolve(Object bean, BeanOperator beanOperator) {
        return bean instanceof Map ?
                MapBeanResolverHandler.getInstance().resolve(bean, beanOperator)
                :
                SimpleBeanResolverHandler.getInstance().resolve(bean, beanOperator);
    }
}