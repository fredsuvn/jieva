package xyz.fslabo.common.mapper;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.mapper.handlers.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class MapperImpl implements Mapper, Mapper.Handler {

    static final MapperImpl DEFAULT_MAPPER = new MapperImpl(Arrays.asList(
        new AssignableMapperHandler(),
        new EnumMapperHandler(),
        new TypedMapperHandler(),
        new CollectionMappingHandler(),
        new BeanMapperHandler()
    ));

    private final List<Mapper.Handler> handlers;

    MapperImpl(Iterable<Mapper.Handler> handlers) {
        this.handlers = JieColl.toList(handlers);
    }

    @Override
    public List<Handler> getHandlers() {
        return handlers;
    }

    @Override
    public Mapper withFirstHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new MapperImpl(newHandlers);
    }

    @Override
    public Mapper withLastHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(handler);
        return new MapperImpl(newHandlers);
    }

    @Override
    public Mapper replaceFirstHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(0, handler);
        return new MapperImpl(newHandlers);
    }

    @Override
    public Mapper replaceLastHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(newHandlers.size() - 1, handler);
        return new MapperImpl(newHandlers);
    }

    @Override
    public Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Object map(
        @Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        Object result = map(source, sourceType, targetType, options);
        if (result == Flag.UNSUPPORTED) {
            return Flag.CONTINUE;
        }
        return result;
    }

    @Override
    public Object mapProperty(
        @Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
        Object result = mapProperty(source, sourceType, targetType, targetProperty, options);
        if (result == Flag.UNSUPPORTED) {
            return Flag.CONTINUE;
        }
        return result;
    }
}
