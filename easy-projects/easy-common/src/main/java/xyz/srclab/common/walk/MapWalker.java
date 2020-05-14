package xyz.srclab.common.walk;

import java.util.Map;

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
        map.forEach((k, v) -> visitor.visit(k, v, walkerProvider));
    }
}
