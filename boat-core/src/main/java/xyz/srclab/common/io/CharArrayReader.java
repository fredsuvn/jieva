package xyz.srclab.common.io;

import xyz.srclab.common.lang.MgCheck;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * {@link Reader} that wraps as a char array.
 *
 * @author fredsuvn
 */
public class CharArrayReader extends Reader {

    private final char[] source;
    private final int sourceOffset;
    private final int sourceEndIndex;
    private int next;
    private int mark;

    /**
     * Wraps given char array as a {@link Reader} from given offset to given length long.
     *
     * @param source given char array
     * @param offset given offset
     * @param length given length long
     */
    public CharArrayReader(char[] source, int offset, int length) {
        MgCheck.checkRangeInBounds(offset, offset + length, 0, source.length);
        this.source = source;
        this.sourceOffset = offset;
        this.sourceEndIndex = offset + length;
        this.next = offset;
        this.mark = offset;
    }

    @Override
    public int read(CharBuffer target) throws IOException {
        if (isEnd()) {
            return -1;
        }
        int len = target.remaining();
        if (len <= 0) {
            return 0;
        }
        int read = Math.min(len, sourceEndIndex - next);
        target.put(source, next, read);
        next += read;
        return read;
    }

    @Override
    public int read() throws IOException {
        if (isEnd()) {
            return -1;
        }
        char c = source[next];
        next++;
        return c & 0x0000ffff;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        MgCheck.checkRangeInBounds(off, off + len, 0, cbuf.length);
        if (isEnd()) {
            return -1;
        }
        if (len <= 0) {
            return 0;
        }
        int read = Math.min(len, sourceEndIndex - next);
        System.arraycopy(source, next, cbuf, off, read);
        next += read;
        return read;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public long skip(long n) {
        MgCheck.checkArgument(n >= 0, n + "");
        int rest = sourceEndIndex - next;
        if (n <= rest) {
            next += n;
            return n;
        } else {
            next += rest;
            return rest;
        }
    }

    @Override
    public boolean ready() throws IOException {
        return true;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        mark = next;
    }

    @Override
    public void reset() throws IOException {
        next = mark;
    }

    /**
     * Sets current read point to given position from 0 to wrapped length. The index of next read char is
     * {@code 'pos + offset-of-wrapped-char-array'}.
     * <p>
     * This method can let you re-read this reader from given position,
     * if the position is 0, you can re-read whole reader.
     *
     * @param pos given position
     */
    public void reset(int pos) {
        MgCheck.checkInBounds(pos, 0, sourceEndIndex - sourceOffset);
        next = sourceOffset + pos;
    }

    protected boolean isEnd() {
        return next >= sourceEndIndex;
    }
}
