package xyz.srclab.common.string;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.lang.computed.Computed;
import xyz.srclab.common.walk.Walker;

import java.util.LinkedList;
import java.util.function.Supplier;

@Immutable
public class ToString extends Computed<String> {

    public static String toString(Object any) {
        return new ToString(any).toString();
    }

    public static String toString(Object any, ToStringStyle style) {
        return new ToString(any, style).toString();
    }

    public static String toString(Object any, ToStringStyle style, BeanOperator beanOperator) {
        return new ToString(any, style, beanOperator).toString();
    }

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
        return get();
    }

    private static final class ToStringSupplier implements Supplier<String> {

        private final Object any;
        private final ToStringStyle style;
        private final BeanOperator beanOperator;

        private ToStringSupplier(Object any, ToStringStyle style, BeanOperator beanOperator) {
            this.any = any;
            this.style = style;
            this.beanOperator = beanOperator;
        }

        @Override
        public String get() {
            if (!style.getDeepToStringPredicate().test(any)) {
                return String.valueOf(any);
            }
            ToStringVisitor toStringVisitor = new ToStringVisitor(style);
            Walker.withBeanOperator(beanOperator).walk(any, toStringVisitor);
            return toStringVisitor.getBuffer().toString();
        }
    }

    private static final class ToStringVisitor implements WalkVisitor {

        protected final ToStringStyle toStringStyle;
        protected final StringBuilder buffer = new StringBuilder();
        protected final LinkedList<Object> valueStackTrace = new LinkedList<>();
        protected final LinkedList<Object> indexStackTrace = new LinkedList<>();
        protected int level = 0;

        private ToStringVisitor(ToStringStyle toStringStyle) {
            this.toStringStyle = toStringStyle;
        }

        public StringBuilder getBuffer() {
            return buffer;
        }

        @Override
        public WalkVisitResult visit(Object index, @Nullable Object value, WalkerProvider walkerProvider) {
            if (Walker.ROOT_INDEX != index) {
                writeWrapping();
                writeIndent(level);
                writeObject(index);
                writeIndicator();
            }

            if (value == null || !toStringStyle.getDeepToStringPredicate().test(value)) {
                writeObject(value);
                writeSeparator();
                return WalkVisitResult.CONTINUE;
            }

            if (valueStackTrace.contains(value)) {
                if (!toStringStyle.getIgnoreReferenceLoop()) {
                    throw new LoopElementException(indexStackTrace);
                }
                return WalkVisitResult.CONTINUE;
            }

            valueStackTrace.add(value);
            if (Walker.ROOT_INDEX != index) {
                indexStackTrace.add(index);
            }
            Walker walker = walkerProvider.getWalker(value);
            if (value instanceof Iterable || value.getClass().isArray()) {
                writeListStart();
                walker.walk(value, this);
                unWriteSeparator();
                writeListEnd();
            } else {
                writeBeanStart();
                walker.walk(value, this);
                unWriteSeparator();
                writeBeanEnd();
            }
            return WalkVisitResult.CONTINUE;
        }

        protected void writeObject(@Nullable Object any) {
            buffer.append(any);
        }

        protected void writeBeanStart() {
            buffer.append(toStringStyle.getBeanStart());
        }

        protected void writeBeanEnd() {
            buffer.append(toStringStyle.getBeanEnd());
        }

        protected void writeListStart() {
            buffer.append(toStringStyle.getListStart());
        }

        protected void writeListEnd() {
            buffer.append(toStringStyle.getListEnd());
        }

        protected void writeWrapping() {
            if (StringUtils.isEmpty(toStringStyle.getWrapping())) {
                return;
            }
            buffer.append(toStringStyle.getWrapping());
        }

        protected void writeIndent(int level) {
            if (level == 0 || StringUtils.isEmpty(toStringStyle.getIndent())) {
                return;
            }
            for (int i = 0; i < level; i++) {
                buffer.append(toStringStyle.getIndent());
            }
        }

        protected void writeSeparator() {
            buffer.append(toStringStyle.getSeparator());
        }

        protected void unWriteSeparator() {
            int bufferLength = buffer.length();
            int separatorLength = toStringStyle.getSeparator().length();
            buffer.delete(bufferLength - separatorLength, bufferLength);
        }

        protected void writeIndicator() {
            buffer.append(toStringStyle.getIndicator());
        }
    }
}
