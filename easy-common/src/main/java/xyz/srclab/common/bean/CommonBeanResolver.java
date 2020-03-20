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
                MapBeanResolverHandler.getInstance().resolve(bean)
                :
                PojoBeanResolverHandler.getInstance().resolve(bean);
    }
}