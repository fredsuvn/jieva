package xyz.srclab.common.walk;

import java.util.Map;
import java.util.Set;

/**
 * @author sunqian
 */
public class MapWalker implements Walker {

    private final WalkerProvider walkerProvider;

    public MapWalker(WalkerProvider walkerProvider) {
        this.walkerProvider = walkerProvider;
    }

    @Override
    public void walk(Object walked, WalkVisitor visitor) {
        Map<?, ?> map = (Map<?, ?>) walked;
        Set<? extends Map.Entry<?, ?>> set = map.entrySet();
        loop:
        for (Map.Entry<?, ?> entry : set) {
            Object index = entry.getKey();
            Object value = entry.getValue();
            WalkVisitResult result = visitor.visit(index, value, walkerProvider);
            switch (result) {
                case CONTINUE:
                    continue loop;
                case TERMINATE:
                    break loop;
            }
        }
    }
}
