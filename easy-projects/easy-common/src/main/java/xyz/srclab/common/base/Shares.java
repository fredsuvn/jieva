package xyz.srclab.common.base;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

/**
 * @author sunqian
 */
public class Shares {

    public static final CharMatcher DOT_CHAR_MATCHER = CharMatcher.is('.');

    public static final CharMatcher $_CHAR_MATCHER = CharMatcher.is('$');

    public static final Joiner COMMA_JOINER = Joiner.on(", ");
}
