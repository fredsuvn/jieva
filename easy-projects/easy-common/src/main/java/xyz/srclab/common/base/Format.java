package xyz.srclab.common.base;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.helpers.MessageFormatter;
import xyz.srclab.annotation.Out;

import java.text.MessageFormat;
import java.util.Locale;

public class Format {

    public static String fastFormat(String messagePattern, Object... args) {
        return FastFormat.INSTANCE.format(messagePattern, args);
    }

    public static String printfFormat(String messagePattern, Object... args) {
        return String.format(Locale.getDefault(), messagePattern, args);
    }

    public static String messageFormat(String messagePattern, Object... args) {
        return MessageFormat.format(messagePattern, args);
    }

    private static final class FastFormat {

        private static final FastFormat INSTANCE = new FastFormat();

        public String format(String pattern, Object... args) {
            processArguments(args);
            return MessageFormatter.arrayFormat(pattern, args, null).getMessage();
        }

        private void processArguments(@Out Object... args) {
            if (ArrayUtils.isEmpty(args)) {
                return;
            }
            final Object lastEntry = args[args.length - 1];
            if (lastEntry instanceof Throwable) {
                args[args.length - 1] = lastEntry.toString();
            }
        }
    }
}
