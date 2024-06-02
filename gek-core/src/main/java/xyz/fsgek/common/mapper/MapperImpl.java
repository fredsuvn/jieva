package xyz.fsgek.common.mapper;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekFlag;
import xyz.fsgek.common.collect.GekColl;
import xyz.fsgek.common.mapper.handlers.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class MapperImpl implements JieMapper, JieMapper.Handler {

    static final MapperImpl DEFAULT_MAPPER = new MapperImpl(Arrays.asList(
        ReuseConvertHandler.INSTANCE,
        EnumConvertHandler.INSTANCE,
        DateConvertHandler.INSTANCE,
        BytesConvertHandler.INSTANCE,
        BooleanConvertHandler.INSTANCE,
        NumberConvertHandler.INSTANCE,
        StringConvertHandler.INSTANCE,
        CollectConvertHandler.INSTANCE,
        BeanConvertHandler.INSTANCE
    ));

    private final List<JieMapper.Handler> handlers;

    MapperImpl(Iterable<JieMapper.Handler> handlers) {
        this.handlers = GekColl.toList(handlers);
    }

    @Override
    public List<Handler> getHandlers() {
        return handlers;
    }

    @Override
    public JieMapper withFirstHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new MapperImpl(newHandlers);
    }

    @Override
    public JieMapper withLastHandler(Handler handler) {
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
        @Nullable Object source, Type sourceType, Type targetType, JieMapper mapper, JieMapperOption... options) {
        Object result = map(source, sourceType, targetType, options);
        if (result == GekFlag.BREAK) {
            return GekFlag.CONTINUE;
        }
        return result;
    }
}
