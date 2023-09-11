package xyz.srclab.common.convert;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.handlers.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

final class ConverterImpl implements FsConverter, FsConverter.Handler {

    static final ConverterImpl INSTANCE = new ConverterImpl(
        ReuseConvertHandler.INSTANCE,
        BeanConvertHandler.INSTANCE,
        Arrays.asList(
            DateConvertHandler.INSTANCE,
            BooleanConvertHandler.INSTANCE,
            NumberConvertHandler.INSTANCE,
            StringConvertHandler.INSTANCE,
            CollectConvertHandler.INSTANCE
        ),
        FsConverter.defaultOptions()
    );

    private final @Nullable FsConverter.Handler pf;
    private final @Nullable FsConverter.Handler sf;
    private final List<FsConverter.Handler> chs;
    private final FsConverter.Options opts;

    ConverterImpl(@Nullable FsConverter.Handler pf, @Nullable FsConverter.Handler sf, List<FsConverter.Handler> chs, FsConverter.Options opts) {
        this.pf = pf;
        this.sf = sf;
        this.chs = chs;
        this.opts = opts;
    }

    @Override
    public @Nullable FsConverter.Handler getPrefixHandler() {
        return pf;
    }

    @Override
    public @Nullable FsConverter.Handler getSuffixHandler() {
        return sf;
    }

    @Override
    public List<FsConverter.Handler> getCommonHandlers() {
        return chs;
    }

    @Override
    public FsConverter.Options getOptions() {
        return opts;
    }

    @Override
    public FsConverter.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options opts, FsConverter converter) {
        Object value = convertObject(source, sourceType, targetType, opts);
        if (value == Fs.RETURN) {
            return Fs.BREAK;
        }
        return value;
    }
}
