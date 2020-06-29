package xyz.srclab.common.convert;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.convert.handlers.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author sunqian
 */
final class ConvertHandlerSupport {

    @Immutable
    static List<ConvertHandler> defaultHandlers() {
        return ListKit.immutable(Arrays.asList(
                new CastConvertHandler(),
                new DateConvertHandler(),
                new MapConvertHandler(),
                new ListConvertHandler(),
                new SetConvertHandler(),
                new NumberConvertHandler(),
                new StringConvertHandler(),
                new RecordConvertHandler()
        ));
    }
}
