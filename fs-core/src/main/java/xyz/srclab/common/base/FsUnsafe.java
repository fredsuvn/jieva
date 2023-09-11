package xyz.srclab.common.base;

import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.bean.handlers.JavaBeanResolveHandler;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.convert.handlers.*;

import java.util.Arrays;
import java.util.Collections;

/**
 * Holder for FS.
 *
 * @author fredsuvn
 */
public final class FsUnsafe {

    public static final class ForLogger {
        static final FsLogger SYSTEM_LOGGER = FsLogger.ofLevel(FsLogger.Level.INFO);
    }

    public static final class ForBean {
        public static final FsBeanResolver DEFAULT_RESOLVER = FsBeanResolver.newResolver(
            Collections.singletonList(JavaBeanResolveHandler.INSTANCE)
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
