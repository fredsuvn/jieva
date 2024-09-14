package xyz.fslabo.common.reflect;

/**
 * A counter to count stack depth.
 *
 * @author fredsuvn
 */
public class StackCounter {

    private int depth;
    private int maxDepth;

    /**
     * Constructs a new counter.
     */
    public StackCounter() {
        reset();
    }

    /**
     * Increments one depth, returns this.
     *
     * @return this
     */
    public StackCounter increment() {
        depth++;
        if (depth > maxDepth) {
            maxDepth = depth;
        }
        return this;
    }

    /**
     * Decrements one depth, returns this.
     *
     * @return this
     */
    public StackCounter decrement() {
        depth--;
        return this;
    }

    /**
     * Returns current depth.
     *
     * @return current depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Return the max depth that this stack counter has ever reached.
     *
     * @return max depth that this stack counter has ever reached
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    public StackCounter reset() {
        this.depth = 0;
        this.maxDepth = 0;
        return this;
    }
}
