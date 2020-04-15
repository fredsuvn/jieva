package xyz.srclab.common.string.tostring;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotations.Immutable;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.bean.BeanStruct;
import xyz.srclab.common.lang.Computed;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@Immutable
public class ToString implements Computed<String> {

    public static String buildToString(@Nullable Object any) {
        if (any == null || TypeHelper.isBasic(any)) {
            return String.valueOf(any);
        }
        return new ToString(any).toString();
    }

    public static String buildToString(@Nullable Object any, ToStringStyle style) {
        if (any == null || TypeHelper.isBasic(any)) {
            return String.valueOf(any);
        }
        return new ToString(any, style).toString();
    }

    public static String buildToString(@Nullable Object any, ToStringStyle style, BeanOperator beanOperator) {
        if (any == null || TypeHelper.isBasic(any)) {
            return String.valueOf(any);
        }
        return new ToString(any, style, beanOperator).toString();
    }

    private final Object any;
    private final ToStringStyle style;
    private final BeanOperator beanOperator;

    private @Nullable String cache;

    public ToString(Object any) {
        this(any, ToStringStyle.DEFAULT, BeanOperator.DEFAULT);
    }

    public ToString(Object any, ToStringStyle style) {
        this(any, style, BeanOperator.DEFAULT);
    }

    public ToString(Object any, ToStringStyle style, BeanOperator beanOperator) {
        this.any = any;
        this.style = style;
        this.beanOperator = beanOperator;
    }

    @Override
    public String get() {
        if (cache == null) {
            return refreshGet();
        }
        return cache;
    }

    @Override
    public String refreshGet() {
        if (TypeHelper.isBasic(any)) {
            return String.valueOf(any);
        }
        StringBuilder buffer = new StringBuilder();
        ToStringContext context = new ToStringContext();
        buildToString(any, buffer, context);
        cache = buffer.toString();
        return cache;
    }

    @Override
    public String toString() {
        return get();
    }

    private void buildToString(@Nullable Object object, StringBuilder buffer, ToStringContext context) {
        if (object == null || TypeHelper.isBasic(object)) {
            buffer.append(object instanceof Type ? ((Type) object).getTypeName() : object);
            return;
        }

        if (!style.getIgnoreReferenceLoop() && context.hasParent(object)) {
            throw new PropertyOrElementReferenceLoopException(context.getNameStackTrace());
        }

        if (object instanceof Object[]) {
            buildArrayToString((Object[]) object, buffer, context);
            return;
        }
        if (object instanceof Iterable) {
            buildIterableToString((Iterable<Object>) object, buffer, context);
            return;
        }
        if (object instanceof Map) {
            buildMapToString((Map<Object, Object>) object, buffer, context);
            return;
        }
        buildBeanToString(object, buffer, context);
    }

    private void buildBeanToString(Object bean, StringBuilder buffer, ToStringContext context) {
        writeBeanStart(buffer);
        BeanStruct beanStruct = beanOperator.resolve(bean.getClass());
        context.pushParent(bean);
        context.pushIndent();
        int[] count = {0};
        beanStruct.getReadableProperties().forEach((name, property) -> {
            context.pushName("." + name);
            writeWrapping(buffer);
            writeIndent(buffer, context.getIndent());
            buffer.append(name);
            writeIndicator(buffer);
            buildToString(property.getValue(bean), buffer, context);
            writeSeparator(buffer);
            context.popName();
            count[0]++;
        });
        context.popIndent();
        context.popParent();
        if (count[0] > 0) {
            unWriteSeparator(buffer);
            writeWrapping(buffer);
            writeIndent(buffer, context.getIndent());
        }
        writeBeanEnd(buffer);
    }

    private void buildArrayToString(Object[] array, StringBuilder buffer, ToStringContext context) {
        if (array.length == 0) {
            writeListStart(buffer);
            writeListEnd(buffer);
            return;
        }
        buildIterableToString(Arrays.asList(array), buffer, context);
    }

    private void buildIterableToString(Iterable<Object> iterable, StringBuilder buffer, ToStringContext context) {
        writeListStart(buffer);
        context.pushParent(iterable);
        context.pushIndent();
        int[] count = {0};
        for (Object o : iterable) {
            String name = iterable instanceof Set ? "[" + o + "]" : "[" + count[0] + "]";
            context.pushName(name);
            writeWrapping(buffer);
            writeIndent(buffer, context.getIndent());
            buildToString(o, buffer, context);
            writeSeparator(buffer);
            context.popName();
            count[0]++;
        }
        context.popIndent();
        context.popParent();
        if (count[0] > 0) {
            unWriteSeparator(buffer);
            writeWrapping(buffer);
            writeIndent(buffer, context.getIndent());
        }
        writeListEnd(buffer);
    }

    private void buildMapToString(Map<Object, Object> map, StringBuilder buffer, ToStringContext context) {
        writeBeanStart(buffer);
        context.pushParent(map);
        context.pushIndent();
        int[] count = {0};
        map.forEach((k, v) -> {
            String keyName = "[key:" + String.valueOf(k) + "]";
            context.pushName(keyName);
            writeWrapping(buffer);
            writeIndent(buffer, context.getIndent());
            buildToString(k, buffer, context);
            context.popName();
            context.pushName("." + k);
            writeIndicator(buffer);
            buildToString(v, buffer, context);
            writeSeparator(buffer);
            context.popName();
            count[0]++;
        });
        context.popIndent();
        context.popParent();
        if (count[0] > 0) {
            unWriteSeparator(buffer);
            writeWrapping(buffer);
            writeIndent(buffer, context.getIndent());
        }
        writeBeanEnd(buffer);
    }

    private void writeBeanStart(StringBuilder buffer) {
        buffer.append(style.getBeanStart());
    }

    private void writeBeanEnd(StringBuilder buffer) {
        buffer.append(style.getBeanEnd());
    }

    private void writeListStart(StringBuilder buffer) {
        buffer.append(style.getListStart());
    }

    private void writeListEnd(StringBuilder buffer) {
        buffer.append(style.getListEnd());
    }

    private void writeWrapping(StringBuilder buffer) {
        if (StringUtils.isEmpty(style.getWrapping())) {
            return;
        }
        buffer.append(style.getWrapping());
    }

    private void writeIndent(StringBuilder buffer, int level) {
        if (StringUtils.isEmpty(style.getIndent())) {
            return;
        }
        for (int i = 0; i < level; i++) {
            buffer.append(style.getIndent());
        }
    }

    private void writeSeparator(StringBuilder buffer) {
        buffer.append(style.getSeparator());
    }

    private void unWriteSeparator(StringBuilder buffer) {
        int bufferLength = buffer.length();
        int separatorLength = style.getSeparator().length();
        buffer.delete(bufferLength - separatorLength, bufferLength);
    }

    private void writeIndicator(StringBuilder buffer) {
        buffer.append(style.getIndicator());
    }

    private static final class ToStringContext {

        private final LinkedList<Object> parentStackTrace = new LinkedList<>();
        private final LinkedList<String> nameStackTrace = new LinkedList<>();
        private int indent = 0;

        public void pushParent(Object parent) {
            parentStackTrace.addLast(parent);
        }

        public void popParent() {
            parentStackTrace.removeLast();
        }

        public boolean hasParent(Object any) {
            return parentStackTrace.contains(any);
        }

        public void pushName(String name) {
            nameStackTrace.addLast(name);
        }

        public void popName() {
            nameStackTrace.removeLast();
        }

        public LinkedList<String> getNameStackTrace() {
            return nameStackTrace;
        }

        public void pushIndent() {
            this.indent += 1;
        }

        public void popIndent() {
            this.indent -= 1;
        }

        public int getIndent() {
            return indent;
        }
    }
}
