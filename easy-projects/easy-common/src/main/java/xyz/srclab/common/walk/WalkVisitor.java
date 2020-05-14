package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface WalkVisitor {

    WalkVisitResult visit(Object index, @Nullable Object value, WalkerProvider walkerProvider);
}
