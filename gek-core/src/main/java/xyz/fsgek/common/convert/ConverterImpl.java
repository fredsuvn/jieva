package xyz.fsgek.common.convert;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekFlag;
import xyz.fsgek.common.collect.GekColl;
import xyz.fsgek.common.convert.handlers.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class ConverterImpl implements GekConverter, GekConverter.Handler {

    static final ConverterImpl INSTANCE = new ConverterImpl(
        ReuseConvertHandler.INSTANCE,
        BeanConvertHandler.INSTANCE,
        Arrays.asList(
            EnumConvertHandler.INSTANCE,
            DateConvertHandler.INSTANCE,
            BytesConvertHandler.INSTANCE,
            BooleanConvertHandler.INSTANCE,
            NumberConvertHandler.INSTANCE,
            StringConvertHandler.INSTANCE,
            CollectConvertHandler.INSTANCE
        ),
        GekConverter.defaultOptions()
    );

    private final @Nullable GekConverter.Handler prefixHandler;
    private final @Nullable GekConverter.Handler suffixHandler;
    private final List<GekConverter.Handler> middleHandlers;
    private final GekConverter.Options options;
    private final List<GekConverter.Handler> handlers;

    ConverterImpl(
        @Nullable GekConverter.Handler prefixHandler,
        @Nullable GekConverter.Handler suffixHandler,
        Iterable<GekConverter.Handler> middleHandlers,
        GekConverter.Options options
    ) {
        this.prefixHandler = prefixHandler;
        this.suffixHandler = suffixHandler;
        this.middleHandlers = GekColl.immutableList(middleHandlers);
        this.options = options;
        int total = 0;
        if (this.prefixHandler != null) {
            total++;
        }
        if (this.suffixHandler != null) {
            total++;
        }
        if (GekColl.isNotEmpty(this.middleHandlers)) {
            total += this.middleHandlers.size();
        }
        if (total > 0) {
            ArrayList<GekConverter.Handler> handlers = new ArrayList<>(total);
            if (this.prefixHandler != null) {
                handlers.add(prefixHandler);
            }
            if (GekColl.isNotEmpty(this.middleHandlers)) {
                handlers.addAll(this.middleHandlers);
            }
            if (this.suffixHandler != null) {
                handlers.add(suffixHandler);
            }
            this.handlers = Collections.unmodifiableList(handlers);
        } else {
            this.handlers = Collections.emptyList();
        }
    }

    @Override
    public @Nullable GekConverter.Handler getPrefixHandler() {
        return prefixHandler;
    }

    @Override
    public @Nullable GekConverter.Handler getSuffixHandler() {
        return suffixHandler;
    }

    @Override
    public List<GekConverter.Handler> getMiddleHandlers() {
        return middleHandlers;
    }

    @Override
    public List<Handler> getHandlers() {
        return handlers;
    }

    @Override
    public GekConverter.Options getOptions() {
        return options;
    }

    @Override
    public GekConverter.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, @Nullable GekConverter converter) {
        if (converter == null) {
            converter = this;
        }
        for (Handler handler : getHandlers()) {
            Object value = handler.convert(source, sourceType, targetType, converter);
            if (value == null || value == GekFlag.CONTINUE) {
                continue;
            }
            if (value == GekFlag.BREAK || value == GekFlag.RETURN) {
                return GekFlag.BREAK;
            }
            if (value == GekFlag.NULL) {
                return GekFlag.NULL;
            }
            return value;
        }
        return null;
    }
}
