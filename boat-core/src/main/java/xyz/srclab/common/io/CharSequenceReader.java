package xyz.srclab.common.io;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import xyz.srclab.common.lang.MgCheck;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Wraps source char sequence as {@link Reader} from start index inclusive to end index exclusive.
 * This reader is backed by the source char sequence, that means,
 * any modification to the source char sequence will reflect to this reader.
 *
 * @author fredsuvn
 */
@EqualsAndHashCode
public class CharSequenceReader extends Reader {

    /**
     * Source char sequence.
     */
    @Getter
    private final CharSequence source;
    /**
     * Start index of source char sequence.
     */
    @Getter
    private final int sourceStartIndex;
    /**
     * Start index of source char sequence.
     */
    @Getter
    private final int sourceEndIndex;

    private int next = 0;
    private int mark = 0;

    public CharSequenceReader(CharSequence source, int sourceStartIndex, int sourceEndIndex) {
        this.source = source;
        this.sourceStartIndex = sourceStartIndex;
        this.sourceEndIndex = sourceEndIndex;
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
        int read = 0;
        while (next < source.length()) {
            target.put(source.charAt(next));
            next++;
            read++;
        }
        return read;
    }

    @Override
    public int read() throws IOException {
        if (isEnd()) {
            return -1;
        }
        char c = source.charAt(next);
        next++;
        return c & 0x0000ffff;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        MgCheck.checkRangeInBounds(off, off + len, 0, cbuf.length);
        if (isEnd()) {
            return -1;
        }
        int read = 0;
        while (read < len && next < source.length()) {
            cbuf[read + off] = source.charAt(next);
            next++;
            read++;
        }
        next += read;
        return read;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public long skip(long n) {
        MgCheck.checkArgument(n >= 0, n + "");
        int rest = source.length() - next;
        if (n <= rest) {
            next += n;
            return n;
        } else {
            next = source.length();
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

    protected boolean isEnd() {
        return next >= source.length();
    }
}
