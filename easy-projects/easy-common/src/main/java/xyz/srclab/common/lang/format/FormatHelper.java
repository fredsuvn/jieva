package xyz.srclab.common.lang.format;

import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.lang.format.fastformat.FastFormatter;

import java.text.MessageFormat;

public class FormatHelper {

    private static final Formatter fastFormatter = new FastFormatter();

    public static String fastFormat(String messagePattern, Object... args) {
        return fastFormatter.format(messagePattern, args);
    }

    public static String printfFormat(String messagePattern, Object... args) {
        return String.format(Defaults.LOCALE, messagePattern, args);
    }

    public static String messageFormat(String messagePattern, Object... args) {
        return MessageFormat.format(messagePattern, args);
    }
}
