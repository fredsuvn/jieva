package xyz.srclab.common.string;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.stack.Stack;
import xyz.srclab.common.walk.WalkHandler;
import xyz.srclab.common.walk.Walker;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
final class ToStringSupport {

    static ToString defaultToString() {
        return DefaultToStringHolder.DEFAULT;
    }

    static ToString defaultHumanReadable() {
        return DefaultToStringHolder.HUMAN_READABLE;
    }

    static ToString newToString(ToStringStyle toStringStyle) {
        return new ToStringImpl(toStringStyle);
    }

    private static final class ToStringImpl implements ToString {

        private final Walker<ToStringContext> walker;

        public ToStringImpl(ToStringStyle style) {
            ToStringHandler toStringHandler = style.ignoreCircularReference() ?
                    new ToStringHandler(style) : new AcyclicToStringHandler(style);
            this.walker = Walker.newBuilder(toStringHandler)
                    .recorder(style.recorder())
                    .unitPredicate(style.unitPredicate())
                    .build();
        }

        @Override
        public String toString(@Nullable Object any) {
            if (any == null) {
                return String.valueOf((Object) null);
            }
            return walker.walk(any).buffer().toString();
        }
    }

    private static class ToStringHandler implements WalkHandler<ToStringContext> {

        private final ToStringStyle toStringStyle;

        private ToStringHandler(ToStringStyle toStringStyle) {
            this.toStringStyle = toStringStyle;
        }

        @Override
        public ToStringContext newContext() {
            return new ToStringContext();
        }

        @Override
        public ToStringContext newContext(ToStringContext lastContext) {
            return new ToStringContext(lastContext);
        }

        @Override
        public void doUnit(@Nullable Object unit, Type type, Stack<ToStringContext> contextStack) {
            writeUnit(unit, contextStack.topNonNull());
        }

        @Override
        public void beforeList(@Nullable Object list, Type type, Stack<ToStringContext> contextStack) {
            ToStringContext context = contextStack.topNonNull();
            writeListStart(context);
            writeWrapping(context);
        }

        @Override
        public void doListElement(
                int index, @Nullable Object value, Type type,
                Stack<ToStringContext> contextStack, Walker<ToStringContext> walker
        ) {
            ToStringContext context = contextStack.topNonNull();
            writeIndent(context);
            walker.walk(value, type);
            writeSeparator(context);
            context.increaseElementCount();
        }

        @Override
        public void afterList(@Nullable Object list, Type type, Stack<ToStringContext> contextStack) {
            ToStringContext context = contextStack.topNonNull();
            unWriteSeparator(context);
            writeListEnd(context);
        }

        @Override
        public void beforeObject(@Nullable Object record, Type type, Stack<ToStringContext> contextStack) {
            ToStringContext context = contextStack.topNonNull();
            writeObjectStart(context);
            writeWrapping(context);
        }

        @Override
        public void doObjectElement(
                Object index, Type indexType, @Nullable Object value, Type type,
                Stack<ToStringContext> contextStack, Walker<ToStringContext> walker
        ) {
            ToStringContext context = contextStack.topNonNull();
            writeIndent(context);
            walker.walk(index, indexType);
            writeIndicator(context);
            walker.walk(value, type);
            writeSeparator(context);
            context.increaseElementCount();
        }

        @Override
        public void afterObject(@Nullable Object object, Type type, Stack<ToStringContext> contextStack) {
            ToStringContext context = contextStack.topNonNull();
            unWriteSeparator(context);
            writeObjectEnd(context);
        }

        private void writeUnit(@Nullable Object any, ToStringContext context) {
            context.buffer().append(any);
        }

        private void writeObjectStart(ToStringContext context) {
            context.buffer().append(toStringStyle.objectStart());
        }

        private void writeObjectEnd(ToStringContext context) {
            context.buffer().append(toStringStyle.objectEnd());
        }

        private void writeListStart(ToStringContext context) {
            context.buffer().append(toStringStyle.listStart());
        }

        private void writeListEnd(ToStringContext context) {
            context.buffer().append(toStringStyle.listEnd());
        }

        private void writeWrapping(ToStringContext context) {
            if (StringUtils.isEmpty(toStringStyle.wrapping())) {
                return;
            }
            context.buffer().append(toStringStyle.wrapping());
        }

        private void writeIndent(ToStringContext context) {
            if (context.level() == 0 || StringUtils.isEmpty(toStringStyle.indent())) {
                return;
            }
            for (int i = 0; i < context.level(); i++) {
                context.buffer().append(toStringStyle.indent());
            }
        }

        private void writeSeparator(ToStringContext context) {
            context.buffer().append(toStringStyle.separator());
        }

        private void unWriteSeparator(ToStringContext context) {
            if (context.elementCount() <= 0) {
                return;
            }
            int bufferLength = context.buffer().length();
            int separatorLength = toStringStyle.separator().length();
            context.buffer().delete(bufferLength - separatorLength, bufferLength);
        }

        private void writeIndicator(ToStringContext context) {
            context.buffer().append(toStringStyle.indicator());
        }
    }

    private static final class AcyclicToStringHandler extends ToStringHandler {

        private final Stack<Object> indexStack = Stack.newStack();
        private final Stack<Object> valueStack = Stack.newStack();

        private AcyclicToStringHandler(ToStringStyle toStringStyle) {
            super(toStringStyle);
        }

        @Override
        public void doUnit(@Nullable Object unit, Type type, Stack<ToStringContext> contextStack) {
            if (unit != null && valueStack.search(unit)) {
                throw new CircularReferenceException(indexStack.toList());
            }
            super.doUnit(unit, type, contextStack);
        }

        @Override
        public void beforeList(@Nullable Object list, Type type, Stack<ToStringContext> contextStack) {
            if (list != null && valueStack.search(list)) {
                throw new CircularReferenceException(indexStack.toList());
            }
            if (list != null) {
                valueStack.push(list);
            }
            super.beforeList(list, type, contextStack);
        }

        @Override
        public void doListElement(
                int index, @Nullable Object value, Type type,
                Stack<ToStringContext> contextStack, Walker<ToStringContext> walker
        ) {
            indexStack.push("[" + index + "]");
            super.doListElement(index, value, type, contextStack, walker);
            indexStack.pop();
        }

        @Override
        public void afterList(@Nullable Object list, Type type, Stack<ToStringContext> contextStack) {
            super.afterList(list, type, contextStack);
            if (list != null) {
                valueStack.pop();
            }
        }

        @Override
        public void beforeObject(@Nullable Object object, Type type, Stack<ToStringContext> contextStack) {
            if (object != null && valueStack.search(object)) {
                throw new CircularReferenceException(indexStack.toList());
            }
            if (object != null) {
                valueStack.push(object);
            }
            super.beforeObject(object, type, contextStack);
        }

        @Override
        public void doObjectElement(
                Object index, Type indexType, @Nullable Object value, Type type,
                Stack<ToStringContext> contextStack, Walker<ToStringContext> walker
        ) {
            indexStack.push(index);
            super.doObjectElement(index, indexType, value, type, contextStack, walker);
            indexStack.pop();
        }

        @Override
        public void afterObject(@Nullable Object object, Type type, Stack<ToStringContext> contextStack) {
            super.afterObject(object, type, contextStack);
            if (object != null) {
                valueStack.pop();
            }
        }
    }

    private static final class ToStringContext {

        private final StringBuilder buffer;
        private final int level;
        private int elementCount;

        public ToStringContext() {
            this.buffer = new StringBuilder();
            this.level = 0;
            this.elementCount = 0;
        }

        public ToStringContext(ToStringContext lastContext) {
            this.buffer = lastContext.buffer;
            this.level = lastContext.level() + 1;
            this.elementCount = 0;
        }

        public StringBuilder buffer() {
            return buffer;
        }

        public int level() {
            return level;
        }

        public int elementCount() {
            return elementCount;
        }

        public void increaseElementCount() {
            this.elementCount++;
        }
    }

    private static final class DefaultToStringHolder {

        public static final ToString DEFAULT = ToString.style(ToStringStyle.DEFAULT);

        public static final ToString HUMAN_READABLE = ToString.style(ToStringStyle.HUMAN_READABLE);
    }
}
