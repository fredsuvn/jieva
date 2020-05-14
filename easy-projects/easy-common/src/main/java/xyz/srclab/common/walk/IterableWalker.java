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
        loop:
        for (Object o : iterable) {
            WalkVisitResult result = visitor.visit(i, o, walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
            i++;
        }
    }
}
