package xyz.srclab.common.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class MapBeanResolverHandler implements BeanResolverHandler {

    public static MapBeanResolverHandler getInstance() {
        return INSTANCE;
    }

    private static final MapBeanResolverHandler INSTANCE = new MapBeanResolverHandler();

    private static final ThreadLocal<WeakHashMap<String, MapAsBeanPropertyDescriptor>> descriptorCache =
            ThreadLocal.withInitial(WeakHashMap::new);

    @Override
    public boolean supportBean(Object bean, BeanOperator beanOperator) {
        return bean instanceof Map;
    }

    @Override
    public BeanDescriptor resolve(Object bean, BeanOperator beanOperator) {
        if (!(bean instanceof Map)) {
            throw new IllegalArgumentException("Given bean must be a Map.");
        }
        return BeanDescriptor.newBuilder()
                .setType(Map.class)
                .setProperties(buildProperties((Map) bean))
                .build();
    }

    private Map<String, BeanPropertyDescriptor> buildProperties(Map bean) {
        Map<String, BeanPropertyDescriptor> map = new HashMap<>();
        for (Object key : bean.keySet()) {
            map.put(String.valueOf(key),
                    descriptorCache.get().computeIfAbsent(String.valueOf(key), MapAsBeanPropertyDescriptor::new)
            );
        }
        return map;
    }

    private static class MapAsBeanPropertyDescriptor implements BeanPropertyDescriptor {

        private final Object key;

        private MapAsBeanPropertyDescriptor(Object key) {
            this.key = key;
        }

        @Override
        public String getName() {
            return key.toString();
        }

        @Override
        public Class<?> getType() {
            return Object.class;
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public Object getValue(Object bean) throws UnsupportedOperationException {
            if (bean instanceof Map) {
                return ((Map) bean).get(key);
            }
            throw new IllegalArgumentException("Given bean must be a Map.");
        }

        @Override
        public boolean isWriteable() {
            return true;
        }

        @Override
        public void setValue(Object bean, Object value) throws UnsupportedOperationException {
            if (!(bean instanceof Map)) {
                throw new IllegalArgumentException("Given bean must be a Map.");
            }
            ((Map) bean).put(key, value);
        }
    }
}