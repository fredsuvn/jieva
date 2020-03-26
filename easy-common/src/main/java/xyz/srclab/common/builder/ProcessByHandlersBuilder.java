package xyz.srclab.common.builder;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class ProcessByHandlersBuilder<
        Processor,
        Handler,
        Builder extends ProcessByHandlersBuilder<Processor, Handler, Builder>> extends CacheStateBuilder<Processor> {

    protected LinkedList<Handler> handlers = new LinkedList<>();

    public Builder addHandler(Handler handler) {
        this.changeState();
        handlers.addFirst(handler);
        return (Builder) this;
    }

    public Builder addHandlers(Handler... handlers) {
        this.changeState();
        return addHandlers(Arrays.asList(handlers));
    }

    public Builder addHandlers(Iterable<Handler> handlers) {
        this.changeState();
        for (Handler handler : handlers) {
            this.handlers.addFirst(handler);
        }
        return (Builder) this;
    }
}
