package xyz.srclab.common.lang.stack;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.ListKit;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public interface Stack<E> {

    static <E> Stack<E> newStack() {
        return StackSupport.newStack();
    }

    static <E> Stack<E> threadSafeStack() {
        return StackSupport.newThreadSafeStack();
    }

    void push(E e);

    @Nullable
    E pop();

    default E popNonNull() throws NoSuchElementException {
        return Require.nonNull(pop());
    }

    @Nullable
    E top();

    default E topNonNull() throws NoSuchElementException {
        return Require.nonNull(top());
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    int size();

    void clear();

    @Immutable
    default List<E> toList() {
        if (isEmpty()) {
            return ListKit.empty();
        }
        int i = size();
        List<E> result = new ArrayList<>(i);
        while (i > 0) {
            result.add(popNonNull());
            i--;
        }
        return ListKit.unmodifiable(result);
    }

    boolean search(E element);
}
