package xyz.srclab.common.design.builder;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.collection.ListKit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class HandlersBuilder<Product, Handler, Builder
        extends HandlersBuilder<Product, Handler, Builder>> extends CachedBuilder<Product> {

    private @Nullable List<Handler> handlers;
    private @Nullable @Immutable List<Handler> result;

    public Builder handler(Handler handler) {
        handlers().add(handler);
        this.updateState();
        return Cast.as(this);
    }

    public Builder handlers(Handler... handlers) {
        return handlers(Arrays.asList(handlers));
    }

    public Builder handlers(Iterable<Handler> handlers) {
        for (Handler handler : handlers) {
            this.handlers().add(handler);
        }
        this.updateState();
        return Cast.as(this);
    }

    protected List<Handler> handlers() {
        if (handlers == null) {
            handlers = new LinkedList<>();
        }
        return handlers;
    }

    @Immutable
    protected List<Handler> handlersResult() {
        if (result == null || isUpdateSinceLastBuild()) {
            result = newResult();
        }
        return result;
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
