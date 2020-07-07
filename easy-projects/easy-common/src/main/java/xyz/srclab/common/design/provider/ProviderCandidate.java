package xyz.srclab.common.design.provider;

import xyz.srclab.annotation.Nullable;

import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public interface ProviderCandidate<T> {

    boolean test();

    @Nullable
    String name();

    T getProvider() throws NoSuchElementException;
}
