package xyz.srclab.common.base;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import xyz.srclab.annotations.Nullable;

import java.io.Serializable;
import java.io.Writer;

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

    private static final int CHAR_BUFFER_SIZE = 16;

    private Node head = null;
    private Node current = null;

    @Override
    public void write(@NotNull char[] cbuf, int off, int len) {
        append(new String(cbuf, off, len));
    }

    @Override
    public void write(int c) {
        append((char) c);
    }

    @Override
    public void write(@NotNull char[] cbuf) {
        append(new String(cbuf));
    }

    @Override
    public void write(@NotNull String str) {
        append(str);
    }

    @Override
    public void write(@NotNull String str, int off, int len) {
        append(str.substring(off, off + len));
    }

    @Override
    public StringAppender append(CharSequence csq) {
        if (current == null) {
            current = new Node(csq);
            head = current;
        } else {
            Node newNode = new Node(csq);
            current.next = newNode;
            current = newNode;
        }
        return this;
    }

    @Override
    public StringAppender append(CharSequence csq, int start, int end) {
        return append(csq.subSequence(start, end).toString());
    }

    @Override
    public StringAppender append(char c) {
        if (current == null) {
            char[] buffer = new char[CHAR_BUFFER_SIZE + 1];
            buffer[0] = c;
            //store the size
            buffer[CHAR_BUFFER_SIZE] = 1;
            current = new Node(buffer);
            head = current;
        } else {
            Node oldNode = current;
            if (oldNode.value instanceof char[]) {
                char[] buffer = (char[]) oldNode.value;
                char count = buffer[CHAR_BUFFER_SIZE];
                if (count < CHAR_BUFFER_SIZE) {
                    //means buffer has space
                    buffer[count] = c;
                    buffer[CHAR_BUFFER_SIZE] = (char) (count + 1);
                } else {
                    //means buffer is full
                    char[] newBuffer = new char[CHAR_BUFFER_SIZE + 1];
                    newBuffer[0] = c;
                    newBuffer[CHAR_BUFFER_SIZE] = 1;
                    Node newNode = new Node(newBuffer);
                    current.next = newNode;
                    current = newNode;
                }
            } else {
                char[] newBuffer = new char[CHAR_BUFFER_SIZE + 1];
                newBuffer[0] = c;
                newBuffer[CHAR_BUFFER_SIZE] = 1;
                Node newNode = new Node(newBuffer);
                current.next = newNode;
                current = newNode;
            }
        }
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
        int size = 0;
        Node node = head;
        while (node != null) {
            if (node.value instanceof char[]) {
                char[] buffer = (char[]) node.value;
                size += buffer[CHAR_BUFFER_SIZE];
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
                char[] buffer = (char[]) node.value;
                System.arraycopy(buffer, 0, chars, charsOffset, buffer[CHAR_BUFFER_SIZE]);
                charsOffset += buffer[CHAR_BUFFER_SIZE];
            } else {
                String value = String.valueOf(node.value);
                value.getChars(0, value.length(), chars, charsOffset);
                charsOffset += value.length();
            }
            node = node.next;
        }
        return new String(chars);
    }

    @Data
    private static class Node implements Serializable {
        private final Object value;
        private @Nullable Node next;
    }
}
