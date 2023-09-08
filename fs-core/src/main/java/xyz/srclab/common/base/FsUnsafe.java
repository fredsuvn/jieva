package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.bean.handlers.DefaultBeanResolveHandler;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.convert.handlers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

/**
 * Holder for FS.
 *
 * @author fredsuvn
 */
public final class FsUnsafe {

    public static final class ForLogger {

        static final FsLogger SYSTEM_LOGGER = FsLogger.ofLevel(FsLogger.INFO_LEVEL);

        @Nullable
        public static void log(FsLogger logger, int level, Object... message) {
            if (level < logger.getLevel()) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            StackTraceElement stackTraceElement = Fs.findStackTraceCaller(
                ForLogger.class.getName(), "internalLog", 1);
            FsLogger.LogMessage log = new FsLogger.LogMessage(level, now, stackTraceElement, message);
            logger.output(log);
        }
    }

    public static final class ForBean {
        public static final FsBeanResolver DEFAULT_RESOLVER = FsBeanResolver.newResolver(
            Collections.singletonList(new DefaultBeanResolveHandler())
        );
    }

    public static final class ForConvert {
        public static final FsConverter.Options DEFAULT_OPTIONS =
            FsConverter.Options.withReusePolicy(FsConverter.Options.REUSE_ASSIGNABLE);
        public static final FsConverter DEFAULT_CONVERTER = FsConverter.withHandlers(
            new ReuseConvertHandler(),
            new BeanConvertHandler(),
            Arrays.asList(
                new DateConvertHandler(),
                new BooleanConvertHandler(),
                new NumberConvertHandler(),
                new StringConvertHandler(),
                new CollectConvertHandler()
            )
        );
    }
}
