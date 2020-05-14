package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.pattern.builder.CachedBuilder;

/**
 * @author sunqian
 */
public interface Walker {

    Object ROOT_INDEX = new Object();

    static Builder newBuilder() {
        return new Builder();
    }

    void walk(Object walked, WalkVisitor visitor);

    class Builder extends CachedBuilder<Walker> {

        private @Nullable WalkerProvider walkerProvider;

        public Builder setWalkerProvider(WalkerProvider walkerProvider) {
            this.walkerProvider = walkerProvider;
            return this;
        }

        @Override
        protected Walker buildNew() {
            if (walkerProvider == null) {
                walkerProvider = WalkerProvider.DEFAULT;
            }
            return new WalkerImpl(walkerProvider);
        }

        private static final class WalkerImpl implements Walker {

            private final WalkerProvider walkerProvider;

            private WalkerImpl(WalkerProvider walkerProvider) {
                this.walkerProvider = walkerProvider;
            }

            @Override
            public void walk(Object walked, WalkVisitor visitor) {
                visitor.visit(ROOT_INDEX, walked, walkerProvider);
            }
        }
    }
}
