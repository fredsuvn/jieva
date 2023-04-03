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

    private Node head = null;
    private Node cur = null;
    //to concat simple char node as a char[] or String node
    private Node charStart = null;

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
        return append0(String.valueOf(csq));
    }

    @Override
    public StringAppender append(CharSequence csq, int start, int end) {
        return append(csq.subSequence(start, end).toString());
    }

    @Override
    public StringAppender append(char c) {
        Node charNode = new Node(c, null);
        if (charStart == null) {
            charStart = charNode;
        }
        return appendNode(charNode);
    }

    /**
     * Appends char values of given char array to this appender.
     *
     * @param chars given char array
     */
    public StringAppender append(char[] chars) {
        return append0(Arrays.copyOf(chars, chars.length));
    }

    /**
     * Appends char values of given char array to this appender.
     *
     * @param chars given char array
     * @param start start index, inclusive
     * @param end   end index, exclusive
     */
    public StringAppender append(char[] chars, int start, int end) {
        return append0(Arrays.copyOfRange(chars, start, end));
    }

    /**
     * Appends string value of given object to this Appendable.
     *
     * @param obj given object
     */
    public StringAppender append(Object obj) {
        return append0(String.valueOf(obj));
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    private StringAppender append0(Object obj) {
        if (charStart != null && !(obj instanceof Character)) {
            //concat chars
            Node charNow = charStart;
            int charCount = 0;
            while (charNow != null) {
                charCount++;
                charNow = charNow.next;
            }
            char[] concatBuf = new char[charCount];
            charNow = charStart;
            charCount = 0;
            while (charNow != null) {
                concatBuf[charCount++] = (Character) charNow.value;
                charNow = charNow.next;
            }
            charStart.value = concatBuf;
            cur = charStart;
            charStart.next = null;
            charStart = null;
        }
        return appendNode(new Node(obj, null));
    }

    private StringAppender appendNode(Node node) {
        if (cur == null) {
            cur = node;
            head = cur;
        } else {
            cur.next = node;
            cur = node;
        }
        return this;
    }

    @Override
    public String toString() {
        int size = 0;
        Node node = head;
        while (node != null) {
            if (node.value instanceof char[]) {
                size += ((char[]) node.value).length;
            } else {
                size += String.valueOf(node.value).length();
            }
            node = node.next;
        }
        char[] chars = new char[size];
        int charsOffset = 0;
        node = head;
        while (node != null) {
            if (node.value instanceof char[]) {
                System.arraycopy(node.value, 0, chars, charsOffset, ((char[]) node.value).length);
                charsOffset += ((char[]) node.value).length;
            } else {
                String value = String.valueOf(node.value);
                value.getChars(0, value.length(), chars, charsOffset);
                charsOffset += value.length();
            }
            node = node.next;
        }
        return new String(chars);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class Node implements Serializable {
        private Object value;
        private @Nullable Node next;
    }
}
