package xyz.srclab.common.base;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.helpers.MessageFormatter;
import xyz.srclab.annotation.NotNull;

import java.text.MessageFormat;
import java.util.Locale;

public class Format {

    @NotNull
    public static String fastFormat(@NotNull String messagePattern, Object... args) {
        return FastFormat.INSTANCE.format(messagePattern, args);
    }

    @NotNull
    public static String printfFormat(@NotNull String messagePattern, Object... args) {
        return String.format(Locale.getDefault(), messagePattern, args);
    }

    @NotNull
    public static String messageFormat(@NotNull String messagePattern, Object... args) {
        return MessageFormat.format(messagePattern, args);
    }

    private static final class FastFormat {

        public static final FastFormat INSTANCE = new FastFormat();

        @NotNull
        public String format(@NotNull String pattern, Object... args) {
            processArguments(args);
            return MessageFormatter.arrayFormat(pattern, args, null).getMessage();
        }

        private void processArguments(Object... args) {
            if (ArrayUtils.isEmpty(args)) {
                return;
            }
            final Object lastElement = args[args.length - 1];
            if (lastElement instanceof Throwable) {
                args[args.length - 1] = lastElement.toString();
            }
        }
    }
}
