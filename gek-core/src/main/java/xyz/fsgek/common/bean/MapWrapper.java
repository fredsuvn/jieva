package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.reflect.GekReflect;
import xyz.fsgek.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

final class MapWrapper extends AbstractMap<String, GekProperty> implements GekBean {

    private static final Type DEFAULT_MAP_BEAN_TYPE = new TypeRef<Map<String, ?>>() {
    }.getType();

    private final Map<String, ?> source;
    private final Type mapType;
    private final @Nullable Type valueType;

    MapWrapper(Map<String, ?> source, @Nullable Type mapType) {
        this.source = source;
        this.mapType = Gek.notNull(mapType, DEFAULT_MAP_BEAN_TYPE);
        if (Objects.equals(this.mapType, DEFAULT_MAP_BEAN_TYPE)) {
            this.valueType = null;
        } else {
            ParameterizedType parameterizedType = GekReflect.getGenericSuperType(mapType, Map.class);
            if (parameterizedType == null) {
                throw new IllegalArgumentException("Not a map type: " + mapType + ".");
            }
            Type[] types = parameterizedType.getActualTypeArguments();
            if (!Objects.equals(String.class, types[0])) {
                throw new IllegalArgumentException("Key type is not String: " + types[0] + ".");
            }
            this.valueType = types[1];
        }
    }

    @Override
    public Type getType() {
        return mapType;
    }

    @Override
    public Map<String, GekProperty> getProperties() {
        return this;
    }

    @Override
    public int size() {
        return source.size();
    }

    @Override
    public boolean isEmpty() {
        return source.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return source.containsKey(key);
    }

    @Override
    public GekProperty get(Object key) {
        if (!source.containsKey(key)) {
            return null;
        }
        return new PropertyWrapper(key.toString());
    }

    @Override
    public Set<Entry<String, GekProperty>> entrySet() {
        return Gek.as(source.entrySet().stream().map(
                it -> new SimpleImmutableEntry<>(it.getKey(), new PropertyWrapper(it.toString())))
            .collect(Collectors.toSet())
        );
    }

    @Override
    public String toString() {
        return GekBean.toString(this);
    }

    final class PropertyWrapper implements GekProperty {

        private final String key;

        private PropertyWrapper(String key) {
            this.key = key;
        }

        @Override
        public String getName() {
            return key;
        }

        @Override
        public @Nullable Object getValue(Object bean) {
            return source.get(key);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) {
            source.put(key, Gek.as(value));
        }

        @Override
        public Type getType() {
            return Gek.notNull(valueType, () -> {
                Object value = source.get(key);
                if (value != null) {
                    return value.getClass();
                }
                return Object.class;
            });
        }

        @Override
        public @Nullable Method getGetter() {
            return null;
        }

        @Override
        public @Nullable Method getSetter() {
            return null;
        }

        @Override
        public @Nullable Field getField() {
            return null;
        }

        @Override
        public List<Annotation> getGetterAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getSetterAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getFieldAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public GekBean getOwner() {
            return MapWrapper.this;
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWriteable() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return GekProperty.equals(this, o);
        }

        @Override
        public int hashCode() {
            return GekProperty.hashCode(this);
        }

        @Override
        public String toString() {
            return GekProperty.toString(this);
        }
    }
}
