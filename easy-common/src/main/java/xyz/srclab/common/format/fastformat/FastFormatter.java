package xyz.srclab.common.format.fastformat;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.format.Formatter;

@ThreadSafe
public class FastFormatter implements Formatter {

    @Override
    public String format(String pattern, Object... args) {
        return new FastFormat(pattern, args).toString();
    }
}
