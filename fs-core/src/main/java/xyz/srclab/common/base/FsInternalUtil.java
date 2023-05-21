package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Internal utilities for fs.
 *
 * @author fredsuvn
 */
class FsInternalUtil {

    @Nullable
    public static void internalLog(FsLogger logger, int level, Object... message) {
        if (level < logger.getLevel()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        StackTraceElement stackTraceElement = Fs.findStackTraceCaller(
            FsInternalUtil.class.getName(), "internalLog", 1);
        FsLogger.Log log = new FsLogger.Log(level, now, stackTraceElement, message);
        logger.output(log);
    }
}
