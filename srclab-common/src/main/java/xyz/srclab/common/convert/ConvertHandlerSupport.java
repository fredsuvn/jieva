package xyz.srclab.common.convert;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.convert.handlers.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author sunqian
 */
final class ConvertHandlerSupport {

    @Immutable
    static List<ConvertHandler> defaultHandlers() {
        return DefaultHandlersHolder.INSTANCES;
    }

    private static final class DefaultHandlersHolder {

        public static final List<ConvertHandler> INSTANCES = ListOps.immutable(Arrays.asList(
                new CastConvertHandler(),
                new DateConvertHandler(),
                new StringConvertHandler(),
                new NumberConvertHandler(),
                new TypeBoundsConvertHandler(),
                new ListConvertHandler(),
                new SetConvertHandler(),
                new RecordConvertHandler(t -> true)
        ));
    }
}
