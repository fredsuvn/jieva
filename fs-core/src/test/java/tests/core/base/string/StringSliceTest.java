package tests.core.base.string;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.string.StringSlice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringSliceTest {

    @Test
    public void testStringSlice() {
        String string = "abcde";
        StringSlice slice = StringSlice.of(string, 1, 4);

        assertSame(string, slice.source());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals('b', slice.charAt(0));
        assertEquals('c', slice.charAt(1));
        assertEquals('d', slice.charAt(2));

        assertEquals("bcd", slice.toString());
        assertEquals("", StringSlice.of(string, 2, 2).toString());

        assertThrows(IndexOutOfBoundsException.class, () -> slice.charAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.charAt(3));
        assertThrows(IndexOutOfBoundsException.class, () -> StringSlice.of(string, 3, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> StringSlice.of(string, 1, 100));
    }
}