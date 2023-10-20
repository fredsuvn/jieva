package xyz.fsgek.common.convert;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.FsFlag;
import xyz.fsgek.common.collect.FsCollect;
import xyz.fsgek.common.convert.handlers.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class ConverterImpl implements FsConverter, FsConverter.Handler {

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
        FsConverter.defaultOptions()
    );

    private final @Nullable FsConverter.Handler prefixHandler;
    private final @Nullable FsConverter.Handler suffixHandler;
    private final List<FsConverter.Handler> middleHandlers;
    private final FsConverter.Options options;
    private final List<FsConverter.Handler> handlers;

    ConverterImpl(
        @Nullable FsConverter.Handler prefixHandler,
        @Nullable FsConverter.Handler suffixHandler,
        Iterable<FsConverter.Handler> middleHandlers,
        FsConverter.Options options
    ) {
        this.prefixHandler = prefixHandler;
        this.suffixHandler = suffixHandler;
        this.middleHandlers = FsCollect.immutableList(middleHandlers);
        this.options = options;
        int total = 0;
        if (this.prefixHandler != null) {
            total++;
        }
        if (this.suffixHandler != null) {
            total++;
        }
        if (FsCollect.isNotEmpty(this.middleHandlers)) {
            total += this.middleHandlers.size();
        }
        if (total > 0) {
            ArrayList<FsConverter.Handler> handlers = new ArrayList<>(total);
            if (this.prefixHandler != null) {
                handlers.add(prefixHandler);
            }
            if (FsCollect.isNotEmpty(this.middleHandlers)) {
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
    public @Nullable FsConverter.Handler getPrefixHandler() {
        return prefixHandler;
    }

    @Override
    public @Nullable FsConverter.Handler getSuffixHandler() {
        return suffixHandler;
    }

    @Override
    public List<FsConverter.Handler> getMiddleHandlers() {
        return middleHandlers;
    }

    @Override
    public List<Handler> getHandlers() {
        return handlers;
    }

    @Override
    public FsConverter.Options getOptions() {
        return options;
    }

    @Override
    public FsConverter.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, @Nullable FsConverter converter) {
        if (converter == null) {
            converter = this;
        }
        for (Handler handler : getHandlers()) {
            Object value = handler.convert(source, sourceType, targetType, converter);
            if (value == null || value == FsFlag.CONTINUE) {
                continue;
            }
            if (value == FsFlag.BREAK || value == FsFlag.RETURN) {
                return FsFlag.BREAK;
            }
            if (value == FsFlag.NULL) {
                return FsFlag.NULL;
            }
            return value;
        }
        return null;
    }
}
