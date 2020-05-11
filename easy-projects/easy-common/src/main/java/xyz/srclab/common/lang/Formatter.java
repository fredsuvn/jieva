package xyz.srclab.common.lang;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.helpers.MessageFormatter;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Written;
import xyz.srclab.common.base.Defaults;

import java.text.MessageFormat;

@Immutable
public abstract class Formatter {

    public static String fastFormat(String messagePattern, Object... args) {
        return FastFormatter.INSTANCE.format(messagePattern, args);
    }

    public static String printfFormat(String messagePattern, Object... args) {
        return String.format(Defaults.LOCALE, messagePattern, args);
    }

    public static String messageFormat(String messagePattern, Object... args) {
        return MessageFormat.format(messagePattern, args);
    }

    public abstract String format(String pattern, Object... args);

    private static final class FastFormatter extends Formatter {

        private static final FastFormatter INSTANCE = new FastFormatter();

        @Override
        public String format(String pattern, Object... args) {
            return new FastFormat(pattern, args).toString();
        }
    }

    @Immutable
    private static final class FastFormat {

        private final String pattern;
        private final Object[] args;

        private @Nullable String toString;

        public FastFormat(String pattern, Object... args) {
            this.pattern = pattern;
            this.args = args;
        }

        @Override
        public String toString() {
            if (toString == null) {
                toString = format0(pattern, args);
            }
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
}
