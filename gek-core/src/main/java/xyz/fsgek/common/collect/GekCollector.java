package xyz.fsgek.common.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * This class is used to configure and build collection in method chaining:
 * <pre>
 *     process.input(in).output(out).start();
 * </pre>
 * Its instance is reusable, re-set and re-build are permitted.
 *
 * @author fredsuvn
 */
public abstract class GekCollector {

    /**
     * Returns a new pair of specified key and value.
     *
     * @param key   specified key
     * @param value specified value
     * @param <K>   key type
     * @param <V>   value type
     * @return a new pair of specified key and value
     */
    public static <K, V> Pair<K, V> pair(K key, V value) {
        return new Pair<K, V>() {
            @Override
            public K pair() {
                return key;
            }

            @Override
            public V value() {
                return value;
            }
        };
    }

    static GekCollector newInstance() {
        return new OfJdk8();
    }

    private int initialSize = 0;
    private Iterable<?> initialElements = null;
    private IntFunction<?> elementFunction = null;
    private boolean immutable = false;

    /**
     * Sets initial size.
     *
     * @param initialSize initial size
     * @return this
     */
    public GekCollector initialSize(int initialSize) {
        this.initialSize = initialSize;
        return this;
    }

    /**
     * Sets initial elements.
     *
     * @param initialElements initial elements
     * @return this
     */
    public GekCollector initialElements(Iterable<?> initialElements) {
        this.initialElements = initialElements;
        return this;
    }

    /**
     * Sets element function:
     * <ul>
     *     <li>
     *         To build a collection, the function will be passed to the index and return an element at the index;
     *     </li>
     *     <li>
     *         To build a map, the function will be passed to the index and return
     *         an array of {@link Pair} at the index;
     *     </li>
     * </ul>
     * If the {@link #initialElements} is set, this configuration will be ignored.
     *
     * @param elementFunction element function
     * @return this
     */
    public GekCollector elementFunction(IntFunction<?> elementFunction) {
        this.elementFunction = elementFunction;
        return this;
    }

    /**
     * Sets whether built collection is immutable.
     *
     * @param immutable whether built collection is immutable
     * @return this
     */
    public GekCollector immutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    public <T> List<T> list() {
        return arrayList();
    }

    public <T> ArrayList<T> arrayList() {
        ArrayList<T> result =
    }

    private static final class OfJdk8 extends GekCollector {
    }

    /**
     * Structure contains a key and a value.
     *
     * @param <K> key type
     * @param <V> value type
     */
    public interface Pair<K, V> {

        /**
         * Returns key.
         *
         * @return key
         */
        K pair();

        /**
         * Returns value.
         *
         * @return value
         */
        V value();
    }
}
