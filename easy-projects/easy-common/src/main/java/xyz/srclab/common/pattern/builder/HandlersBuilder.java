package xyz.srclab.common.pattern.builder;

import xyz.srclab.common.base.Cast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class HandlersBuilder<Product, Handler, Builder
        extends HandlersBuilder<Product, Handler, Builder>> extends CachedBuilder<Product> {

    protected final List<Handler> handlers = new LinkedList<>();

    public Builder handler(Handler handler) {
        handlers.add(handler);
        this.updateState();
        return Cast.as(this);
    }

    public Builder handlers(Handler... handlers) {
        return handlers(Arrays.asList(handlers));
    }

    public Builder handlers(Iterable<Handler> handlers) {
        for (Handler handler : handlers) {
            this.handlers.add(handler);
        }
        this.updateState();
        return Cast.as(this);
    }
}
