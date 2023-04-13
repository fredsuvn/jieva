package xyz.srclab.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.srclab.annotations.Nullable;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;

/**
 * StringAppender is used to append objects and concat them into a string,
 * just like {@link StringBuilder} but faster and lower space and simpler.
 * <p>
 * StringAppender uses a linked list to store the converted strings of objects,
 * and calculates the required capacity of the char array when toString is called.
 * Then it puts the data stored in the linked list into the array to generate the final string,
 * unlike StringBuilder, which expands the capacity by creating a new size array and copying data.
 *
 * @author fredsuvn
 */
public class StringAppender extends Writer implements Appendable {

    private static final int DEFAULT_INIT_CAPACITY = 16;

    private static final int DEFAULT_MAX_CAPACITY = 2048;

    private final int initCapacity;

    private final int maxCapacity;

    private char[][] data;

    private int dataOffset = 0;

    private char[] buffer;

    private int bufferOffset = 0;

    public StringAppender() {
        this(DEFAULT_INIT_CAPACITY, DEFAULT_MAX_CAPACITY);
    }

    public StringAppender(int initCapacity) {
        this(initCapacity, DEFAULT_MAX_CAPACITY);
    }

    public StringAppender(int initCapacity, int maxCapacity) {
        if (initCapacity <= 0 || maxCapacity <= 0) {
            throw new IllegalArgumentException("initCapacity and maxCapacity must > 0!");
        }
        if (initCapacity >= maxCapacity) {
            throw new IllegalArgumentException("initCapacity must < maxCapacity!");
        }
        this.initCapacity = initCapacity;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        append(cbuf, off, off + len);
    }

    @Override
    public void write(int c) {
        append((char) c);
    }

    @Override
    public void write(char[] cbuf) {
        append(cbuf);
    }

    @Override
    public void write(String str) {
        append(str);
    }

    @Override
    public void write(String str, int off, int len) {
        append(str, off, off + len);
    }

    @Override
    public StringAppender append(CharSequence csq) {
        ensureCapacity(csq.length());
        for (int i = 0; i < csq.length(); i++) {
            buffer[bufferOffset++] = csq.charAt(i);
        }
        return this;
    }

    @Override
    public StringAppender append(CharSequence csq, int start, int end) {
        if (end - start < 0) {
            throw new IllegalArgumentException("start must <= end");
        }
        if (start == end) {
            return this;
        }
        ensureCapacity(end - start);
        for (int i = start; i < end; i++) {
            buffer[bufferOffset++] = csq.charAt(i);
        }
        return this;
    }

    @Override
    public StringAppender append(char c) {
        ensureCapacity(1);
        buffer[bufferOffset++] = c;
        return this;
    }

    /**
     * Appends char values of given char array to this appender.
     *
     * @param chars given char array
     */
    public StringAppender append(char[] chars) {
        return append(chars, 0, chars.length);
    }

    /**
     * Appends char values of given char array to this appender.
     *
     * @param chars given char array
     * @param start start index, inclusive
     * @param end   end index, exclusive
     */
    public StringAppender append(char[] chars, int start, int end) {
        if (end - start < 0) {
            throw new IllegalArgumentException("start must <= end");
        }
        if (start == end) {
            return this;
        }
        ensureCapacity(end - start);
        System.arraycopy(chars, start, buffer, bufferOffset, end - start);
        bufferOffset += (end - start);
        return this;
    }

    /**
     * Appends given string to this appender.
     *
     * @param str given string
     */
    public StringAppender append(String str) {
        ensureCapacity(str.length());
        str.getChars(0, str.length(), buffer, bufferOffset);
        bufferOffset += str.length();
        return this;
    }

    /**
     * Appends given string to this appender.
     *
     * @param str   given string
     * @param start start index, inclusive
     * @param end   end index, exclusive
     */
    public StringAppender append(String str, int start, int end) {
        if (end - start < 0) {
            throw new IllegalArgumentException("start must <= end");
        }
        if (start == end) {
            return this;
        }
        ensureCapacity(end - start);
        str.getChars(start, end, buffer, bufferOffset);
        bufferOffset += (end - start);
        return this;
    }

    /**
     * Appends string value of given object to this Appendable.
     *
     * @param obj given object
     */
    public StringAppender append(Object obj) {
        return append(String.valueOf(obj));
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        appendLastNode();
        buffer = null;
        bufferOffset = 0;
        int size = 0;
        for (int i = 0; i < dataOffset; i++) {
            char[] datum = data[i];
            size += datum.length;
        }
        char[] chars = new char[size];
        int charsOffset = 0;
        for (int i = 0; i < dataOffset; i++) {
            char[] datum = data[i];
            System.arraycopy(datum, 0, chars, charsOffset, datum.length);
            charsOffset += datum.length;
        }
        return new String(chars);
    }

    private void ensureCapacity(int appendLength) {
        if (buffer == null) {
            buffer = new char[Math.max(initCapacity, appendLength)];
            return;
        }
        int bufferLength = buffer.length;
        if (appendLength <= bufferLength - bufferOffset) {
            return;
        }
        if (bufferLength == bufferOffset) {
            appendNode();
            buffer = new char[Math.max(initCapacity, appendLength)];
            bufferOffset = 0;
            return;
        }
        int newLength = bufferOffset + appendLength;
        if (newLength >= maxCapacity) {
            buffer = Arrays.copyOf(buffer, newLength);
            return;
        }
        buffer = Arrays.copyOf(buffer, Math.max(buffer.length * 2, newLength));
    }

    private void appendNode() {
        appendNode(buffer);
    }

    private void appendLastNode() {
        if (bufferOffset == 0) {
            return;
        }
        appendNode(Arrays.copyOfRange(buffer, 0, bufferOffset));
    }

    private void appendNode(char[] node) {
        if (data == null) {
            data = new char[8][];
        } else {
            if (dataOffset == data.length) {
                data = Arrays.copyOf(data, data.length * 2);
            }
        }
        data[dataOffset++] = node;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class Node implements Serializable {

        private char[] value;

        @Nullable
        private Node next;
    }
}
