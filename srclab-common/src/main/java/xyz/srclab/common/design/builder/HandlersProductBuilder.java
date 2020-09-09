package xyz.srclab.common.design.builder;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.As;
import xyz.srclab.common.collection.ListKit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class HandlersProductBuilder<Product, Handler, Builder
        extends HandlersProductBuilder<Product, Handler, Builder>> extends BaseProductCachingBuilder<Product> {

    private @Nullable List<Handler> handlers;
    private @Nullable @Immutable List<Handler> handlersResult;

    public Builder handler(Handler handler) {
        handlers().add(handler);
        this.updateState();
        return As.notNull(this);
    }

    public Builder handlers(Handler... handlers) {
        return handlers(Arrays.asList(handlers));
    }

    public Builder handlers(Iterable<Handler> handlers) {
        for (Handler handler : handlers) {
            this.handlers().add(handler);
        }
        this.updateState();
        return As.notNull(this);
    }

    protected List<Handler> handlers() {
        if (handlers == null) {
            handlers = new LinkedList<>();
        }
        return handlers;
    }

    @Immutable
    protected List<Handler> handlersResult() {
        if (handlersResult == null || isUpdatedSinceLastBuild()) {
            handlersResult = newResult();
        }
        return handlersResult;
    }

    @Immutable
    private List<Handler> newResult() {
        if (handlers == null) {
            return ListKit.empty();
        } else {
            return ListKit.immutable(handlers);
        }
    }
}
