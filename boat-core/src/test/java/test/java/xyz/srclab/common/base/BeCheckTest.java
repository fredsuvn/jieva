package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtCheck;

import java.util.NoSuchElementException;

public class BeCheckTest {

    @Test
    public void testCheck() {
        Assert.expectThrows(NullPointerException.class, () ->
            BtCheck.checkNull(false, () -> ""));
        Assert.expectThrows(IllegalArgumentException.class, () ->
            BtCheck.checkArgument(false, () -> ""));
        Assert.expectThrows(IllegalStateException.class, () ->
            BtCheck.checkState(false, () -> ""));
        Assert.expectThrows(NoSuchElementException.class, () ->
            BtCheck.checkElement(false, () -> ""));
        Assert.expectThrows(UnsupportedOperationException.class, () ->
            BtCheck.checkSupported(false, () -> ""));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BtCheck.checkBounds(false, () -> ""));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BtCheck.checkInBounds(0, 1, 2));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BtCheck.checkInBounds(0, 0, 0));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BtCheck.checkInBounds(0, 2, 1));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BtCheck.checkRangeInBounds(0, 4, 2, 10));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BtCheck.checkRangeInBounds(0, 4, 1, 4));
        Assert.expectThrows(IndexOutOfBoundsException.class, () ->
            BtCheck.checkRangeInBounds(0, 4, 0, 3));
        Assert.expectThrows(Exception.class, () -> BtCheck.checkThrow(false, Exception::new));

        BtCheck.checkInBounds(1, 0, 2);
        BtCheck.checkInBounds(0, 0, 1);
        BtCheck.checkRangeInBounds(0, 4, 0, 4);
        BtCheck.checkRangeInBounds(0, 4, -1, 4);
        BtCheck.checkRangeInBounds(2, 4, 0, 4);
    }
}
