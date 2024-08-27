package xyz.fslabo.common.io;

import xyz.fslabo.common.base.JieCheck;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Builder to build an {@link InputStream} whose data stream will be processed through a chain of mappers, and returns
 * the data after all processing. For example:
 * <pre>
 *     InputStream in = ChainStreamBuilder.from(source)
 *         .next(mapper1)
 *         .next(mapper2)
 *         .build();
 * </pre>
 * The stream {@code in} is equivalent to the stream {@code source} whose data will be processed by {@code mapper1} and
 * {@code mapper2}.
 */
public class ChainStreamBuilder {

    /**
     * Returns a new {@link ChainStreamBuilder} whose initial data is specified source.
     *
     * @param source specified source.
     * @return a new {@link ChainStreamBuilder} whose initial data is specified source
     */
    public static ChainStreamBuilder from(InputStream source) {
        return new ChainStreamBuilder(source);
    }

    private final InputStream source;
    private final List<Function<InputStream, InputStream>> mappers = new LinkedList<>();

    private ChainStreamBuilder(InputStream source) {
        JieCheck.checkNull(source);
        this.source = source;
    }

    /**
     * Adds next stream mapper which processes passed {@link InputStream} and returns a mapped {@link InputStream}.
     *
     * @param mapper stream mapper
     * @return this builder
     */
    public ChainStreamBuilder next(Function<InputStream, InputStream> mapper) {
        JieCheck.checkNull(mapper);
        mappers.add(mapper);
        return this;
    }

    /**
     * Builds final {@link InputStream} whose data stream will be processed through the chain of mappers.
     *
     * @return final {@link InputStream} whose data stream will be processed through the chain of mappers
     */
    private InputStream build() {
        InputStream result = source;
        for (Function<InputStream, InputStream> mapper : mappers) {
            result = mapper.apply(result);
        }
        return result;
    }
}
