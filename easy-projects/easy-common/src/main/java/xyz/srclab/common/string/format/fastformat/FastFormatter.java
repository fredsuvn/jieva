package xyz.srclab.common.string.format.fastformat;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.string.format.Formatter;

@ThreadSafe
public class FastFormatter implements Formatter {

    @Override
    public String format(String pattern, Object... args) {
        return FastFormat.format(pattern, args);
    }
}
