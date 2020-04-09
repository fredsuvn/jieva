package xyz.srclab.common.string.format.fastformat;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.helpers.MessageFormatter;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.Computed;

@Immutable
public class FastFormat implements Computed<String> {

    public static String format(String messagePattern, Object... args) {
        return new FastFormat(messagePattern, args).format();
    }

    private final String pattern;
    private final Object[] args;

    private @Nullable String cache;

    public FastFormat(String pattern, Object... args) {
        this.pattern = pattern;
        this.args = args;
    }

    @Override
    public String toString() {
        if (cache == null) {
            return refreshGet();
        }
        return cache;
    }

    private String format() {
        processArguments();
        return MessageFormatter.arrayFormat(pattern, args, null).getMessage();
    }

    private void processArguments() {
        if (ArrayUtils.isEmpty(args)) {
            return;
        }
        final Object lastEntry = args[args.length - 1];
        if (lastEntry instanceof Throwable) {
            args[args.length - 1] = lastEntry.toString();
        }
    }

    @Override
    public String get() {
        return toString();
    }

    @Override
    public String refreshGet() {
        cache = format();
        return cache;
    }
}
