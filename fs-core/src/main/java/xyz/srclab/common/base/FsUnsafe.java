package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Internal utilities for fs.
 *
 * @author fredsuvn
 */
class FsUnsafe {

    private static final FsLogger systemLogger = FsLogger.ofLevel(FsLogger.INFO_LEVEL);

    @Nullable
    public static void log(FsLogger logger, int level, Object... message) {
        if (level < logger.getLevel()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        StackTraceElement stackTraceElement = Fs.findStackTraceCaller(
            FsUnsafe.class.getName(), "internalLog", 1);
        FsLogger.Log log = new FsLogger.Log(level, now, stackTraceElement, message);
        logger.output(log);
    }

    public static FsLogger systemLogger() {
        return systemLogger;
    }
}
