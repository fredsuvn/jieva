package xyz.srclab.common.bean;

public class DefaultBeanResolver implements BeanResolver {

    public static DefaultBeanResolver getInstance() {
        return INSTANCE;
    }

    private static final DefaultBeanResolver INSTANCE = new DefaultBeanResolver();

    @Override
    public BeanDescriptor resolve(Object bean) {
        return DefaultBeanResolverHandler.getInstance().resolve(bean);
    }
}