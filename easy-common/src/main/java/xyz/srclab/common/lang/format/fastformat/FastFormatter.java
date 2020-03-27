package xyz.srclab.common.lang.format.fastformat;

import xyz.srclab.common.lang.format.Formatter;

public class FastFormatter implements Formatter {

    @Override
    public String format(String pattern, Object... args) {
        return new FastFormat(pattern, args).toString();
    }
}
