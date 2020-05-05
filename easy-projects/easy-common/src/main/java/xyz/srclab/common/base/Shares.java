package xyz.srclab.common.base;

import com.google.common.base.CharMatcher;

/**
 * @author sunqian
 */
public class Shares {

    public static final CharMatcher DOT_CHAR_MATCHER = CharMatcher.is('.');

    public static final CharMatcher JAVA_NAMING_MATCHER = CharMatcher.inRange('0', '9')
            .or(CharMatcher.inRange('a', 'z'))
            .or(CharMatcher.inRange('A', 'Z'))
            .or(CharMatcher.anyOf("_$"));

    public static final CharMatcher NON_JAVA_NAMING_MATCHER = JAVA_NAMING_MATCHER.negate();
}
