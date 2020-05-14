package xyz.srclab.common.walk;

/**
 * @author sunqian
 */
public class IterableWalker implements Walker {

    private final WalkerProvider walkerProvider;

    public IterableWalker(WalkerProvider walkerProvider) {
        this.walkerProvider = walkerProvider;
    }

    @Override
    public void walk(Object walked, WalkVisitor visitor) {
        Iterable<?> iterable = (Iterable<?>) walked;
        int i = 0;
        for (Object o : iterable) {
            visitor.visit(i, o, walkerProvider);
            i++;
        }
    }
}
