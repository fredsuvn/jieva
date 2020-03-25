package xyz.srclab.common.bean;

public class CommonBeanResolver implements BeanResolver {

    public static CommonBeanResolver getInstance() {
        return INSTANCE;
    }

    private static final CommonBeanResolver INSTANCE = new CommonBeanResolver();

    @Override
    public BeanDescriptor resolve(Object bean) {
        return CommonBeanResolverHandler.getInstance().resolve(bean);
    }
}