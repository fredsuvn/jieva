package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BCheck;

import java.util.NoSuchElementException;

public class BCheckTest {

    @Test
    public void testCheck() {
        Assert.expectThrows(NullPointerException.class, () ->
            BCheck.checkNull(false, () -> ""));
        Assert.expectThrows(IllegalArgumentException.class, () ->
            BCheck.checkArgument(false, () -> ""));
        Assert.expectThrows(IllegalStateException.class, () ->
            BCheck.checkState(false, () -> ""));
        Assert.expectThrows(NoSuchElementException.class, () ->
            BCheck.checkElement(false, () -> ""));
        Assert.expectThrows(UnsupportedOperationException.class, () ->
            BCheck.checkSupported(false, () -> ""));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BCheck.checkBounds(false, () -> ""));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BCheck.checkInBounds(0, 1, 2));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BCheck.checkInBounds(0, 0, 0));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BCheck.checkInBounds(0, 2, 1));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BCheck.checkRangeInBounds(0, 4, 2, 10));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BCheck.checkRangeInBounds(0, 4, 1, 4));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BCheck.checkRangeInBounds(0, 4, 0, 3));
        Assert.expectThrows(Exception.class, () -> BCheck.checkThrow(false, Exception::new));

        BCheck.checkInBounds(1, 0, 2);
        BCheck.checkInBounds(0, 0, 1);
        BCheck.checkRangeInBounds(0, 4, 0, 4);
        BCheck.checkRangeInBounds(0, 4, -1, 4);
        BCheck.checkRangeInBounds(2, 4, 0, 4);
    }
}
