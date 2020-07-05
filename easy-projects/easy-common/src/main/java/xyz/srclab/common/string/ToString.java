package xyz.srclab.common.string;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.lang.lazy.Lazy;
import xyz.srclab.common.walk.WalkHandler;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class ToString implements Lazy<String> {

    public static String toString(Object any) {
        return new ToString(any).toString();
    }

    public static String toString(Object any, ToStringStyle style) {
        return new ToString(any, style).toString();
    }

    public static String toString(Object any, ToStringStyle style, BeanOperator beanOperator) {
        return new ToString(any, style, beanOperator).toString();
    }

    private final ToStringStyle toStringStyle;
    private @Nullable String toString;

    public ToString(Object any) {
        this(any, ToStringStyle.DEFAULT, BeanOperator.DEFAULT);
    }

    public ToString(Object any, ToStringStyle style) {
        this(any, style, BeanOperator.DEFAULT);
    }

    public ToString(Object any, ToStringStyle style, BeanOperator beanOperator) {
        super(new ToStringSupplier(any, style, beanOperator));
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = get();
        }
        return toString;
    }

    @Override
    public String get() {
        return new ToStringImpl(toStringStyle).toString();
    }

    private static final class ToStringImpl {

        protected final ToStringStyle toStringStyle;
        protected final StringBuilder buffer = new StringBuilder();
        protected final LinkedList<Object> valueStackTrace = new LinkedList<>();
        protected final LinkedList<Object> indexStackTrace = new LinkedList<>();
        protected int level = 0;

        private ToStringImpl(ToStringStyle toStringStyle) {
            this.toStringStyle = toStringStyle;
        }

        public String toString() {

        }


    }

    private static class ToStringHandler implements WalkHandler {

        private final ToStringStyle toStringStyle;
        private final StringBuilder buffer = new StringBuilder();
        private int level = 0;
        private int elementCount = 0;

        private ToStringHandler(ToStringStyle toStringStyle) {
            this.toStringStyle = toStringStyle;
        }

        @Override
        public void doUnit(@Nullable Object unit, Type type) {
            writeUnit(unit);
        }

        @Override
        public void doElement(int index, @Nullable Object value, Type type) {
            elementCount++;
        }

        @Override
        public void doEntry(Object index, Type indexType, @Nullable Object value, Type type) {

        }

        @Override
        public void beforeObject(@Nullable Object record, Type type) {

        }

        @Override
        public void afterObject(@Nullable Object record, Type type) {

        }

        @Override
        public void beforeList(@Nullable Object list, Type type) {

        }

        @Override
        public void afterList(@Nullable Object list, Type type) {

        }

        private StringBuilder buffer() {
            return buffer;
        }

        private void writeUnit(@Nullable Object any) {
            buffer.append(any);
        }

        private void writeObjectStart() {
            buffer.append(toStringStyle.objectStart());
        }

        private void writeObjectEnd() {
            buffer.append(toStringStyle.objectEnd());
        }

        private void writeListStart() {
            buffer.append(toStringStyle.listStart());
        }

        private void writeListEnd() {
            buffer.append(toStringStyle.listEnd());
        }

        private void writeWrapping() {
            if (StringUtils.isEmpty(toStringStyle.wrapping())) {
                return;
            }
            buffer.append(toStringStyle.wrapping());
        }

        private void writeIndent(int level) {
            if (level == 0 || StringUtils.isEmpty(toStringStyle.indent())) {
                return;
            }
            for (int i = 0; i < level; i++) {
                buffer.append(toStringStyle.indent());
            }
        }

        private void writeSeparator() {
            buffer.append(toStringStyle.separator());
        }

        private void unWriteSeparator() {
            if (elementCount <= 0) {
                return;
            }
            int bufferLength = buffer.length();
            int separatorLength = toStringStyle.separator().length();
            buffer.delete(bufferLength - separatorLength, bufferLength);
        }

        private void writeIndicator() {
            buffer.append(toStringStyle.indicator());
        }
    }
}
