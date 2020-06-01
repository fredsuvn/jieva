package xyz.srclab.common.lang.format;

import xyz.srclab.annotation.Immutable;

import java.text.MessageFormat;
import java.util.Locale;

@Immutable
public interface Formatter {

    static String fastFormat(String messagePattern, Object... args) {
        return Format0.getFastFormatter().format(messagePattern, args);
    }

    static String printfFormat(String messagePattern, Object... args) {
        return String.format(Locale.getDefault(), messagePattern, args);
    }

    static String messageFormat(String messagePattern, Object... args) {
        return MessageFormat.format(messagePattern, args);
    }

    String format(String pattern, Object... args);
}
