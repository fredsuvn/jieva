package xyz.srclab.common.util.string.tostring;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.util.pattern.builder.CachedBuilder;

@Immutable
public interface ToStringStyle {

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

        public Builder setBeanStart(String beanStart) {
            this.updateState();
            this.beanStart = beanStart;
            return this;
        }

        public Builder setBeanEnd(String beanEnd) {
            this.updateState();
            this.beanEnd = beanEnd;
            return this;
        }

        public Builder setListStart(String listStart) {
            this.updateState();
            this.listStart = listStart;
            return this;
        }

        public Builder setListEnd(String listEnd) {
            this.updateState();
            this.listEnd = listEnd;
            return this;
        }

        public Builder setWrapping(String wrapping) {
            this.updateState();
            this.wrapping = wrapping;
            return this;
        }

        public Builder setIndent(String indent) {
            this.updateState();
            this.indent = indent;
            return this;
        }

        public Builder setSeparator(String separator) {
            this.updateState();
            this.separator = separator;
            return this;
        }

        public Builder setIndicator(String indicator) {
            this.updateState();
            this.indicator = indicator;
            return this;
        }

        public Builder setIgnoreReferenceLoop(boolean ignoreReferenceLoop) {
            this.updateState();
            this.ignoreReferenceLoop = ignoreReferenceLoop;
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
            };
        }
    }
}
