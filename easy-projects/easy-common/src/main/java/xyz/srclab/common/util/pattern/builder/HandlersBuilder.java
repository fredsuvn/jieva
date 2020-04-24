package xyz.srclab.common.util.pattern.builder;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class HandlersBuilder<Product, Handler, Builder
        extends HandlersBuilder<Product, Handler, Builder>> extends CachedBuilder<Product> {

    protected LinkedList<Handler> handlers = new LinkedList<>();

    public Builder addHandler(Handler handler) {
        handlers.addFirst(handler);
        this.updateState();
        return (Builder) this;
    }

    public Builder addHandlers(Handler... handlers) {
        return addHandlers(Arrays.asList(handlers));
    }

    public Builder addHandlers(Iterable<Handler> handlers) {
        for (Handler handler : handlers) {
            this.handlers.addFirst(handler);
        }
        this.updateState();
        return (Builder) this;
    }
}
