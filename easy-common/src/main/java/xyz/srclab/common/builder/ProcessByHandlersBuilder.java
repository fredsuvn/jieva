package xyz.srclab.common.builder;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class ProcessByHandlersBuilder<
        Processor,
        Handler,
        Builder extends ProcessByHandlersBuilder<Processor, Handler, Builder>> {

    protected LinkedList<Handler> handlers = new LinkedList<>();

    public Builder addHandler(Handler handler) {
        handlers.addFirst(handler);
        return (Builder) this;
    }

    public Builder addHandlers(Handler... handlers) {
        return addHandlers(Arrays.asList(handlers));
    }

    public Builder addHandlers(Iterable<Handler> handlers) {
        for (Handler handler : handlers) {
            this.handlers.addFirst(handler);
        }
        return (Builder) this;
    }

    public abstract Processor build();
}
