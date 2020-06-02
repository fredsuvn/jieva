package xyz.srclab.common.lang.format;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.helpers.MessageFormatter;
import xyz.srclab.annotation.Out;

/**
 * @author sunqian
 */
final class Format0 {

    static Formatter getFastFormatter() {
        return FastFormatter.INSTANCE;
    }

    private static final class FastFormatter implements Formatter {

        private static final Formatter INSTANCE = new FastFormatter();

        @Override
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
