package xyz.srclab.common.chain;

import java.util.Spliterator;

/**
 * @author sunqian
 */
public interface BaseChain<T, S extends BaseChain<T, S>> extends Iterable<T>, AutoCloseable {

    Spliterator<T> spliterator();

    boolean isParallel();

    S sequential();

    S parallel();

    S unordered();

    S onClose(Runnable closeHandler);
}
