package xyz.fsgik.common.convert;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.base.Fs;
import xyz.fsgik.common.collect.FsCollect;
import xyz.fsgik.common.convert.handlers.*;

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
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
        for (Handler handler : getHandlers()) {
            Object value = handler.convert(source, sourceType, targetType, this);
            if (value == Fs.BREAK) {
                return Fs.BREAK;
            }
            if (value != Fs.CONTINUE) {
                return value;
            }
        }
        return Fs.CONTINUE;
    }
}
