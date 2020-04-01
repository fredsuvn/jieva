package xyz.srclab.common.string.format;

import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.string.format.fastformat.FastFormat;

import java.text.MessageFormat;
import java.util.Locale;

public class FormatHelper {

    public static String fastFormat(String messagePattern, Object... args) {
        return FastFormat.format(messagePattern, args);
    }

    public static String printfFormat(String messagePattern, Object... args) {
        return String.format(Defaults.LOCALE, messagePattern, args);
    }

    public static String printfFormat(Locale locale, String messagePattern, Object... args) {
        return String.format(locale, messagePattern, args);
    }

    public static String messageFormat(String messagePattern, Object... args) {
        return MessageFormat.format(messagePattern, args);
    }
}
