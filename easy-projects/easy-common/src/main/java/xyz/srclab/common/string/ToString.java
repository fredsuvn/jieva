package xyz.srclab.common.string;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.lang.lazy.Lazy;
import xyz.srclab.common.lang.stack.Stack;
import xyz.srclab.common.walk.WalkHandler;
import xyz.srclab.common.walk.Walker;

import java.lang.reflect.Type;

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
        if (toStringStyle.ignoreCircularReference()) {
            ToStringContext context =
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
        public void doUnit(@Nullable Object unit, Type type, ToStringContext context) {
            writeUnit(unit, context);
        }

        @Override
        public void beforeList(@Nullable Object list, Type type, ToStringContext context) {
            writeListStart(context);
        }

        @Override
        public void doListElement(
                int index,
                @Nullable Object value, Type type,
                ToStringContext context, Walker<ToStringContext> walker) {
            walker.walk(value, type);
            writeSeparator(context);
            context.increaseElementCount();
        }

        @Override
        public void afterList(@Nullable Object list, Type type, ToStringContext context) {
            unWriteSeparator(context);
            writeListEnd(context);
        }

        @Override
        public void beforeObject(@Nullable Object record, Type type, ToStringContext context) {
            writeObjectStart();
            level++;
        }

        @Override
        public void doObjectElement(
                Object index, Type indexType,
                @Nullable Object value, Type type,
                ToStringContext context, Walker<ToStringContext> walker) {
            walker.walk(index, indexType);
            writeIndicator();
            walker.walk(value, type);
            writeSeparator();
        }

        @Override
        public void afterObject(@Nullable Object object, Type type, ToStringContext context) {
            level--;
            unWriteSeparator(stack);
            writeObjectEnd();
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
        public void doUnit(@Nullable Object unit, Type type, ToStringContext context) {
            if (unit != null && valueStack.search(unit)) {
                throw new CircularReferenceException(indexStack.toList());
            }
            super.doUnit(unit, type, context);
        }

        @Override
        public void beforeList(@Nullable Object list, Type type, ToStringContext stack) {
            if (list != null && valueStack.search(list)) {
                throw new CircularReferenceException(indexStack.toList());
            }
            if (list != null) {
                valueStack.push(list);
            }
            super.beforeList(list, type, stack);
        }

        @Override
        public void doListElement(
                int index,
                @Nullable Object value, Type type,
                ToStringContext stack, Walker walker) {
            indexStack.push("[" + index + "]");
            super.doListElement(index, value, type, stack, walker);
            indexStack.pop();
        }

        @Override
        public void afterList(@Nullable Object list, Type type, ToStringContext stack) {
            super.afterList(list, type, stack);
            if (list != null) {
                valueStack.pop();
            }
        }

        @Override
        public void beforeObject(@Nullable Object object, Type type, ToStringContext stack) {
            if (object != null && valueStack.search(object)) {
                throw new CircularReferenceException(indexStack.toList());
            }
            if (object != null) {
                valueStack.push(object);
            }
            super.beforeObject(object, type, stack);
        }

        @Override
        public void doObjectElement(
                Object index, Type indexType,
                @Nullable Object value, Type type,
                ToStringContext stack, Walker walker) {
            indexStack.push(index);
            super.doObjectElement(index, indexType, value, type, stack, walker);
            indexStack.pop();
        }

        @Override
        public void afterObject(@Nullable Object object, Type type, ToStringContext stack) {
            super.afterObject(object, type, stack);
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
}
