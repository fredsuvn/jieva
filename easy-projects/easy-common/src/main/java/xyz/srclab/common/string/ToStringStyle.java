package xyz.srclab.common.string;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.design.builder.CachedBuilder;
import xyz.srclab.common.reflect.ClassKit;

import java.util.function.Predicate;

@Immutable
public interface ToStringStyle {

    static Builder newBuilder() {
        return new Builder();
    }

    ToStringStyle DEFAULT = new Builder().build();

    ToStringStyle HUMAN_READABLE = new Builder()
            .setBeanStart("{")
            .setBeanEnd("}")
            .setSeparator(", ")
            .setIndicator(" = ")
            .setIndent("    ")
            .setListStart("[")
            .setListEnd("]")
            .setWrapping(Defaults.LINE_SEPARATOR)
            .setIgnoreReferenceLoop(false)
            .build();

    String getBeanStart();

    String getBeanEnd();

    String getListStart();

    String getListEnd();

    String getWrapping();

    String getIndent();

    String getSeparator();

    String getIndicator();

    boolean getIgnoreReferenceLoop();

    Predicate<Object> getDeepToStringPredicate();

    class Builder extends CachedBuilder<ToStringStyle> {

        private String beanStart = "{";
        private String beanEnd = "}";
        private String listStart = "[";
        private String listEnd = "]";
        private String wrapping = "";
        private String indent = "";
        private String separator = ",";
        private String indicator = "=";
        private boolean ignoreReferenceLoop = false;
        private Predicate<Object> deepToStringPredicate = o -> !ClassKit.isBasic(o);

        public Builder setBeanStart(String beanStart) {
            this.beanStart = beanStart;
            this.updateState();
            return this;
        }

        public Builder setBeanEnd(String beanEnd) {
            this.beanEnd = beanEnd;
            this.updateState();
            return this;
        }

        public Builder setListStart(String listStart) {
            this.listStart = listStart;
            this.updateState();
            return this;
        }

        public Builder setListEnd(String listEnd) {
            this.listEnd = listEnd;
            this.updateState();
            return this;
        }

        public Builder setWrapping(String wrapping) {
            this.wrapping = wrapping;
            this.updateState();
            return this;
        }

        public Builder setIndent(String indent) {
            this.indent = indent;
            this.updateState();
            return this;
        }

        public Builder setSeparator(String separator) {
            this.separator = separator;
            this.updateState();
            return this;
        }

        public Builder setIndicator(String indicator) {
            this.indicator = indicator;
            this.updateState();
            return this;
        }

        public Builder setIgnoreReferenceLoop(boolean ignoreReferenceLoop) {
            this.ignoreReferenceLoop = ignoreReferenceLoop;
            this.updateState();
            return this;
        }

        public Builder setDeepToStringPredicate(Predicate<Object> deepToStringPredicate) {
            this.deepToStringPredicate = deepToStringPredicate;
            return this;
        }

        @Override
        protected ToStringStyle buildNew() {
            return new ToStringStyle() {

                private final String beanStart = Builder.this.beanStart;
                private final String beanEnd = Builder.this.beanEnd;
                private final String listStart = Builder.this.listStart;
                private final String listEnd = Builder.this.listEnd;
                private final String wrapping = Builder.this.wrapping;
                private final String indent = Builder.this.indent;
                private final String separator = Builder.this.separator;
                private final String indicator = Builder.this.indicator;
                private final boolean ignoreReferenceLoop = Builder.this.ignoreReferenceLoop;
                private final Predicate<Object> deepToStringPredicate = Builder.this.deepToStringPredicate;

                @Override
                public String getBeanStart() {
                    return beanStart;
                }

                @Override
                public String getBeanEnd() {
                    return beanEnd;
                }

                @Override
                public String getListStart() {
                    return listStart;
                }

                @Override
                public String getListEnd() {
                    return listEnd;
                }

                @Override
                public String getWrapping() {
                    return wrapping;
                }

                @Override
                public String getIndent() {
                    return indent;
                }

                @Override
                public String getSeparator() {
                    return separator;
                }

                @Override
                public String getIndicator() {
                    return indicator;
                }

                @Override
                public boolean getIgnoreReferenceLoop() {
                    return ignoreReferenceLoop;
                }

                @Override
                public Predicate<Object> getDeepToStringPredicate() {
                    return deepToStringPredicate;
                }
            };
        }
    }
}
