package xyz.srclab.common.string;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;
import xyz.srclab.common.object.UnitPredicate;
import xyz.srclab.common.record.Recorder;

@Immutable
public interface ToStringStyle {

    static Builder newBuilder() {
        return new Builder();
    }

    ToStringStyle DEFAULT = new Builder().build();

    ToStringStyle HUMAN_READABLE = new Builder()
            .objectStart("{")
            .objectEnd("}")
            .separator(", ")
            .indicator(" = ")
            .indent("    ")
            .listStart("[")
            .listEnd("]")
            .wrapping(Environment.currentEnvironment().osLineSeparator())
            .ignoreCircularReference(false)
            .build();

    String objectStart();

    String objectEnd();

    String listStart();

    String listEnd();

    String wrapping();

    String indent();

    String separator();

    String indicator();

    boolean ignoreCircularReference();

    UnitPredicate unitPredicate();

    Recorder recorder();

    class Builder extends BaseProductCachingBuilder<ToStringStyle> {

        private String objectStart = "{";
        private String objectEnd = "}";
        private String listStart = "[";
        private String listEnd = "]";
        private String wrapping = "";
        private String indent = "";
        private String separator = ",";
        private String indicator = "=";
        private boolean ignoreCircularReference = false;
        private UnitPredicate unitPredicate = UnitPredicate.defaultPredicate();
        private Recorder recorder = Recorder.defaultRecorder();

        public Builder objectStart(String objectStart) {
            this.objectStart = objectStart;
            this.updateState();
            return this;
        }

        public Builder objectEnd(String objectEnd) {
            this.objectEnd = objectEnd;
            this.updateState();
            return this;
        }

        public Builder listStart(String listStart) {
            this.listStart = listStart;
            this.updateState();
            return this;
        }

        public Builder listEnd(String listEnd) {
            this.listEnd = listEnd;
            this.updateState();
            return this;
        }

        public Builder wrapping(String wrapping) {
            this.wrapping = wrapping;
            this.updateState();
            return this;
        }

        public Builder indent(String indent) {
            this.indent = indent;
            this.updateState();
            return this;
        }

        public Builder separator(String separator) {
            this.separator = separator;
            this.updateState();
            return this;
        }

        public Builder indicator(String indicator) {
            this.indicator = indicator;
            this.updateState();
            return this;
        }

        public Builder ignoreCircularReference(boolean ignoreCircularReference) {
            this.ignoreCircularReference = ignoreCircularReference;
            this.updateState();
            return this;
        }

        public Builder unitPredicate(UnitPredicate unitPredicate) {
            this.unitPredicate = unitPredicate;
            return this;
        }

        public Builder recorder(Recorder recorder) {
            this.recorder = recorder;
            return this;
        }

        @Override
        protected ToStringStyle buildNew() {
            return new ToStringStyle() {

                private final String objectStart = Builder.this.objectStart;
                private final String objectEnd = Builder.this.objectEnd;
                private final String listStart = Builder.this.listStart;
                private final String listEnd = Builder.this.listEnd;
                private final String wrapping = Builder.this.wrapping;
                private final String indent = Builder.this.indent;
                private final String separator = Builder.this.separator;
                private final String indicator = Builder.this.indicator;
                private final boolean ignoreCircularReference = Builder.this.ignoreCircularReference;
                private final UnitPredicate unitPredicate = Builder.this.unitPredicate;
                private final Recorder recorder = Builder.this.recorder;

                @Override
                public String objectStart() {
                    return objectStart;
                }

                @Override
                public String objectEnd() {
                    return objectEnd;
                }

                @Override
                public String listStart() {
                    return listStart;
                }

                @Override
                public String listEnd() {
                    return listEnd;
                }

                @Override
                public String wrapping() {
                    return wrapping;
                }

                @Override
                public String indent() {
                    return indent;
                }

                @Override
                public String separator() {
                    return separator;
                }

                @Override
                public String indicator() {
                    return indicator;
                }

                @Override
                public boolean ignoreCircularReference() {
                    return ignoreCircularReference;
                }

                @Override
                public UnitPredicate unitPredicate() {
                    return unitPredicate;
                }

                @Override
                public Recorder recorder() {
                    return recorder;
                }
            };
        }

        public ToStringStyle build() {
            return buildCaching();
        }
    }
}
