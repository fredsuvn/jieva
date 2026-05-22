package tests.core.base.chars;

import internal.utils.DataGen;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.chars.CharsBuilder;
import space.sunqian.fs.base.string.StringView;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CharsBuilderTest implements DataGen {

    @Test
    public void testCharsBuilder() {
        testCharsBuilder(new CharsBuilder(), new StringBuilder());
        for (int i = 1; i < 8; i++) {
            CharsBuilder builder = new CharsBuilder(i);
            testCharsBuilder(builder, new StringBuilder());
            builder.reset();
            builder.flush();
            builder.close();
            testCharsBuilder(builder, new StringBuilder());
        }
        testCharsBuilder(new CharsBuilder(666), new StringBuilder());
        testCharsBuilder(new CharsBuilder(1024, 16), new StringBuilder());
        {
            // Test Exception
            assertThrows(IllegalArgumentException.class, () -> new CharsBuilder(0));
            assertThrows(IllegalArgumentException.class, () -> new CharsBuilder(-1));
            assertThrows(IllegalArgumentException.class, () ->
                new CharsBuilder(1, -2));
            assertThrows(IllegalArgumentException.class, () ->
                new CharsBuilder(1, 0));
            assertThrows(IllegalArgumentException.class, () ->
                new CharsBuilder(-1, 2));
            CharsBuilder builder = new CharsBuilder();
            assertThrows(IndexOutOfBoundsException.class, () -> builder.write(new char[0], 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> builder.write("", 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> builder.append(new char[0], 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> builder.append("", 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> builder.append(StringView.of(""), 0, 1));
        }
        {
            // Special
            CharsBuilder charsBuilder = new CharsBuilder(8);
            StringBuilder stringBuilder = new StringBuilder();
            char[] d1 = randomChars(8);
            for (char c : d1) {
                charsBuilder.append(c);
                stringBuilder.append(c);
            }
            String str = new String(d1);
            charsBuilder.append(str);
            stringBuilder.append(str);
            assertEquals(stringBuilder.toString(), charsBuilder.toString());
            for (char c : d1) {
                charsBuilder.append(c);
                stringBuilder.append(c);
            }
            charsBuilder.append(str, 0, 4);
            stringBuilder.append(str, 0, 4);
            assertEquals(stringBuilder.toString(), charsBuilder.toString());
            charsBuilder.append(d1[0]);
            stringBuilder.append(d1[0]);
            assertEquals(stringBuilder.toString(), charsBuilder.toString());
        }
    }

    private void testCharsBuilder(CharsBuilder charsBuilder, StringBuilder stringBuilder) {
        charsBuilder.append('a');
        stringBuilder.append('a');
        char[] d1 = randomChars(8);
        char[] d2 = randomChars(256);
        charsBuilder.append((int) d1[0]);
        stringBuilder.append(d1[0]);
        charsBuilder.append(d1);
        stringBuilder.append(d1);
        charsBuilder.append(d2);
        stringBuilder.append(d2);
        charsBuilder.append(new char[0]);
        stringBuilder.append(new char[0]);
        charsBuilder.append(new char[1], 0, 0);
        stringBuilder.append(new char[1], 0, 0);
        charsBuilder.append(d2, 8, 16);
        stringBuilder.append(d2, 8, 16);
        charsBuilder.append(d2, 6, 222);
        stringBuilder.append(d2, 6, 222);
        charsBuilder.append(StringView.of(d1));
        stringBuilder.append(StringView.of(d1));
        charsBuilder.append(StringView.of(d2));
        stringBuilder.append(StringView.of(d2));
        charsBuilder.append(StringView.of(d2), 223, 233);
        stringBuilder.append(StringView.of(d2, 223, 233));
        charsBuilder.append(StringView.of(d2), 23, 233);
        stringBuilder.append(StringView.of(d2, 23, 233));
        charsBuilder.append(StringView.of(d1, 0, 0));
        stringBuilder.append(StringView.of(d1, 0, 0));
        charsBuilder.append(new String(d1));
        stringBuilder.append(new String(d1));
        charsBuilder.append(new String(d2));
        stringBuilder.append(new String(d2));
        charsBuilder.append(new String(d2), 223, 233);
        stringBuilder.append(new String(d2), 223, 233);
        charsBuilder.append(new String(d2), 23, 233);
        stringBuilder.append(new String(d2), 23, 233);
        charsBuilder.append("");
        stringBuilder.append("");
        charsBuilder.write("");
        stringBuilder.append("");
        charsBuilder.write("123");
        stringBuilder.append("123");
        charsBuilder.append("", 0, 0);
        stringBuilder.append("", 0, 0);
        charsBuilder.write("", 0, 0);
        charsBuilder.append((CharSequence) null);
        stringBuilder.append((CharSequence) null);
        charsBuilder.append((CharSequence) null, 1, 2);
        stringBuilder.append((CharSequence) null, 1, 2);
        charsBuilder.append(CharBuffer.allocate(0));
        stringBuilder.append(CharBuffer.allocate(0));
        {
            // buffer
            CharBuffer buf1 = CharBuffer.wrap(d1, 1, 5);
            assertEquals(5, buf1.remaining());
            CharBuffer buf2 = CharBuffer.wrap(d1, 1, 5);
            charsBuilder.append(buf1);
            assertEquals(0, buf1.remaining());
            stringBuilder.append(buf2);
            byte[] bb = "0123456789".getBytes(StandardCharsets.UTF_8);
            ByteBuffer bb1 = ByteBuffer.allocateDirect(bb.length);
            bb1.put(bb);
            bb1.flip();
            ByteBuffer bb2 = ByteBuffer.allocateDirect(bb.length);
            bb2.put(bb);
            bb2.flip();
            CharBuffer buf3 = bb1.asCharBuffer();
            CharBuffer buf4 = bb2.asCharBuffer();
            assertEquals(5, buf3.remaining());
            charsBuilder.append(buf3);
            assertEquals(0, buf3.remaining());
            stringBuilder.append(buf4);
        }
        assertEquals(charsBuilder.length(), stringBuilder.length());
        assertEquals(charsBuilder.toString(), stringBuilder.toString());
        assertArrayEquals(charsBuilder.toCharArray(), stringBuilder.toString().toCharArray());
        assertEquals(charsBuilder.toCharBuffer(), CharBuffer.wrap(stringBuilder.toString().toCharArray()));
    }
}
