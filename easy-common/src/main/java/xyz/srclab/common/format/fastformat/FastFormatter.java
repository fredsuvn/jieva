package xyz.srclab.common.format.fastformat;

import xyz.srclab.common.format.Formatter;

public class FastFormatter implements Formatter {

    @Override
    public String format(String pattern, Object... args) {
        return new FastFormat(pattern, args).toString();
    }
}
