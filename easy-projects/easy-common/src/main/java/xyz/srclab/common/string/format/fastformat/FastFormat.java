package xyz.srclab.common.string.format.fastformat;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.helpers.MessageFormatter;
import xyz.srclab.annotations.Immutable;
import xyz.srclab.annotations.Written;

@Immutable
public class FastFormat {

    public static String format(String messagePattern, Object... args) {
        return new FastFormat(messagePattern, args).toString();
    }

    private final String toString;

    public FastFormat(String pattern, Object... args) {
        this.toString = format0(pattern, args);
    }

    @Override
    public String toString() {
        return toString;
    }

    private String format0(String pattern, Object... args) {
        processArguments(args);
        return MessageFormatter.arrayFormat(pattern, args, null).getMessage();
    }

    private void processArguments(@Written Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return;
        }
        final Object lastEntry = args[args.length - 1];
        if (lastEntry instanceof Throwable) {
            args[args.length - 1] = lastEntry.toString();
        }
    }
}
