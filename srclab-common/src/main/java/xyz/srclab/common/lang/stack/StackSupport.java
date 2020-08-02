package xyz.srclab.common.lang.stack;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Equal;

import java.util.List;

/**
 * @author sunqian
 */
final class StackSupport {

    static <E> Stack<E> newStack() {
        return new StackImpl<>();
    }

    static <E> Stack<E> newThreadSafeStack() {
        return new SynchronisedStackImpl<>();
    }

    private static class StackImpl<E> implements Stack<E> {

        private int size = 0;
        private @Nullable Node<E> last = null;

        @Override
        public void push(E e) {
            if (last == null) {
                last = new Node<>(e);
            } else {
                Node<E> newNode = new Node<>(e);
                newNode.setPrev(last);
                last = newNode;
            }
            size++;
        }

        @Override
        public E pop() {
            if (last == null) {
                return null;
            }
            Node<E> last = this.last;
            this.last = last.getPrev();
            size--;
            return last.getValue();
        }

        @Override
        public E top() {
            @Nullable Node<E> last = this.last;
            if (last == null) {
                return null;
            }
            return last.getValue();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            last = null;
        }

        @Override
        public boolean search(E element) {
            @Nullable Node<E> last = this.last;
            while (last != null) {
                if (Equal.equals(element, last.getValue())) {
                    return true;
                }
                last = last.getPrev();
            }
            return false;
        }
    }

    private static final class SynchronisedStackImpl<E> extends StackImpl<E> {

        @Override
        public void push(E e) {
            synchronized (this) {
                super.push(e);
            }
        }

        @Override
        public E pop() {
            synchronized (this) {
                return super.pop();
            }
        }

        @Override
        public void clear() {
            synchronized (this) {
                super.clear();
            }
        }

        @Override
        public @Immutable List<E> toList() {
            synchronized (this) {
                return super.toList();
            }
        }

        @Override
        public boolean search(E element) {
            synchronized (this) {
                return super.search(element);
            }
        }
    }

    private static final class Node<E> {

        private final E value;
        private @Nullable Node<E> prev;

        private Node(E value) {
            this.value = value;
        }

        public E getValue() {
            return value;
        }

        @Nullable
        public Node<E> getPrev() {
            return prev;
        }

        public void setPrev(Node<E> prev) {
            this.prev = prev;
        }
    }
}
