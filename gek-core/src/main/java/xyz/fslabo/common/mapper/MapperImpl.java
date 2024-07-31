package xyz.fslabo.common.mapper;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.collect.JieColl;
import xyz.fslabo.common.mapper.handlers.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class MapperImpl implements Mapper, Mapper.Handler {

    static final MapperImpl DEFAULT_MAPPER = new MapperImpl(Arrays.asList(
        AssignableMapperHandler.INSTANCE,
        EnumConvertHandler.INSTANCE,
        DateConvertHandler.INSTANCE,
        BytesConvertHandler.INSTANCE,
        BooleanConvertHandler.INSTANCE,
        NumberMapperHandler.INSTANCE,
        ToStringHandler.INSTANCE,
        CollectConvertHandler.INSTANCE,
        BeanConvertHandler.INSTANCE
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
    public Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Object map(
        @Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MapperOptions options) {
        Object result = mapObject(source, sourceType, targetType, options);
        if (result == Flag.UNSUPPORTED) {
            return Flag.CONTINUE;
        }
        return result;
    }
}
