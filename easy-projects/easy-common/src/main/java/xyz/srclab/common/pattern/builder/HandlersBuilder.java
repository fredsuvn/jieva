package xyz.srclab.common.pattern.builder;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class HandlersBuilder<Product, Handler, Builder
        extends HandlersBuilder<Product, Handler, Builder>> extends CachedBuilder<Product> {

    protected LinkedList<Handler> handlers = new LinkedList<>();

    public Builder handler(Handler handler) {
        handlers.addFirst(handler);
        this.updateState();
        return (Builder) this;
    }

    public Builder handlers(Handler... handlers) {
        return handlers(Arrays.asList(handlers));
    }

    public Builder handlers(Iterable<Handler> handlers) {
        for (Handler handler : handlers) {
            this.handlers.addFirst(handler);
        }
        this.updateState();
        return (Builder) this;
    }
}
