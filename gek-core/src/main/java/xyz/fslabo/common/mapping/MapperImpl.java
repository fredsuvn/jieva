package xyz.fslabo.common.mapping;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.mapping.handlers.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

final class MapperImpl implements Mapper, Mapper.Handler {

    static final MapperImpl DEFAULT_MAPPER = new MapperImpl(Arrays.asList(
        new AssignableMapperHandler(),
        new EnumMapperHandler(),
        new TypedMapperHandler(),
        new CollectionMappingHandler(),
        new BeanMapperHandler()
    ), MappingOptions.defaultOptions());

    private final List<Mapper.Handler> handlers;
    private final MappingOptions defaultOptions;

    MapperImpl(Iterable<Mapper.Handler> handlers, MappingOptions defaultOptions) {
        this.handlers = JieColl.toList(handlers);
        this.defaultOptions = defaultOptions;
    }

    @Override
    public List<Handler> getHandlers() {
        return handlers;
    }

    @Override
    public Mapper addFirstHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new MapperImpl(newHandlers, getOptions());
    }

    @Override
    public Mapper addLastHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(handler);
        return new MapperImpl(newHandlers, getOptions());
    }

    @Override
    public Mapper replaceFirstHandler(Handler handler) {
        if (Objects.equals(handlers.get(0), handler)) {
            return this;
        }
        List<Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(0, handler);
        return new MapperImpl(newHandlers, getOptions());
    }

    @Override
    public Mapper replaceLastHandler(Handler handler) {
        if (Objects.equals(handlers.get(handlers.size() - 1), handler)) {
            return this;
        }
        List<Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(newHandlers.size() - 1, handler);
        return new MapperImpl(newHandlers, getOptions());
    }

    @Override
    public MappingOptions getOptions() {
        return defaultOptions;
    }

    @Override
    public Mapper replaceOptions(MappingOptions options) {
        if (Objects.equals(defaultOptions, options)) {
            return this;
        }
        return new MapperImpl(handlers, options);
    }

    @Override
    public Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Object map(
        @Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        Object result = map(source, sourceType, targetType, options);
        if (result == null) {
            return Flag.CONTINUE;
        }
        return result;
    }

    @Override
    public Object mapProperty(
        @Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
        Object result = mapProperty(source, sourceType, targetType, targetProperty, options);
        if (result == null) {
            return Flag.CONTINUE;
        }
        return result;
    }
}
