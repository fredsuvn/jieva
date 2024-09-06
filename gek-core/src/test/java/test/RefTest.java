package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.ref.*;

public class RefTest {

    @Test
    public void testVal() {
        Assert.assertSame(Val.ofNull(), Val.ofNull());
        Object o = new Object();
        Assert.assertSame(Val.of(o).get(), o);
        Assert.assertSame(BooleanVal.ofFalse(), BooleanVal.ofFalse());
        Assert.assertEquals(BooleanVal.of(true).get(), true);
        Assert.assertEquals(BooleanVal.of(true).toWrapper().get(), Boolean.TRUE);
        Assert.assertSame(ByteVal.ofZero(), ByteVal.ofZero());
        Assert.assertEquals(ByteVal.of((byte) 1).get(), 1);
        Assert.assertEquals(ByteVal.of((byte) 1).toWrapper().get(), Byte.valueOf((byte) 1));
        Assert.assertSame(CharVal.ofZero(), CharVal.ofZero());
        Assert.assertEquals(CharVal.of((char) 1).get(), 1);
        Assert.assertEquals(CharVal.of((char) 1).toWrapper().get(), Character.valueOf((char) 1));
        Assert.assertSame(ShortVal.ofZero(), ShortVal.ofZero());
        Assert.assertEquals(ShortVal.of((short) 1).get(), 1);
        Assert.assertEquals(ShortVal.of((short) 1).toWrapper().get(), Short.valueOf((short) 1));
        Assert.assertSame(IntVal.ofZero(), IntVal.ofZero());
        Assert.assertEquals(IntVal.of(1).get(), 1);
        Assert.assertEquals(IntVal.of(1).toWrapper().get(), Integer.valueOf(1));
        Assert.assertSame(LongVal.ofZero(), LongVal.ofZero());
        Assert.assertEquals(LongVal.of(1).get(), 1);
        Assert.assertEquals(LongVal.of(1).toWrapper().get(), Long.valueOf(1));
        Assert.assertSame(FloatVal.ofZero(), FloatVal.ofZero());
        Assert.assertEquals(FloatVal.of(1).get(), 1);
        Assert.assertEquals(FloatVal.of(1).toWrapper().get(), Float.valueOf(1));
        Assert.assertSame(DoubleVal.ofZero(), DoubleVal.ofZero());
        Assert.assertEquals(DoubleVal.of(1).get(), 1);
        Assert.assertEquals(DoubleVal.of(1).toWrapper().get(), Double.valueOf(1));
    }

    @Test
    public void testVar() {
        Assert.assertNotEquals(Var.ofNull(), Var.ofNull());
        Object o = new Object();
        Assert.assertSame(Var.of(o).get(), o);

        Assert.assertNotEquals(BooleanVar.ofFalse(), BooleanVar.ofFalse());
        Assert.assertEquals(BooleanVar.of(true).get(), true);
        Assert.assertEquals(BooleanVar.of(true).set(false).get(), false);
        Assert.assertEquals(BooleanVar.of(true).toggleAndGet(), false);
        BooleanVar bool = BooleanVar.of(true);
        Assert.assertEquals(bool.getAndToggle(), true);
        Assert.assertEquals(bool.get(), false);
        Assert.assertEquals(BooleanVar.of(true).toWrapper().get(), Boolean.TRUE);

        Assert.assertNotEquals(ByteVar.ofZero(), ByteVar.ofZero());
        Assert.assertEquals(ByteVar.of((byte) 1).get(), 1);
        Assert.assertEquals(ByteVar.of((byte) 1).set((byte) 2).get(), 2);
        Assert.assertEquals(ByteVar.of((byte) 1).add(1).get(), 2);
        Assert.assertEquals(ByteVar.of((byte) 1).getAndIncrement(), 1);
        Assert.assertEquals(ByteVar.of((byte) 1).incrementAndGet(), 2);
        ByteVar bv = ByteVar.of((byte) 1);
        Assert.assertEquals(bv.getAndIncrement(), 1);
        Assert.assertEquals(bv.get(), 2);
        Assert.assertEquals(ByteVar.of((byte) 1).toWrapper().get(), Byte.valueOf((byte) 1));

        Assert.assertNotEquals(CharVar.ofZero(), CharVar.ofZero());
        Assert.assertEquals(CharVar.of((char) 1).get(), 1);
        Assert.assertEquals(CharVar.of((char) 1).set((char) 2).get(), 2);
        Assert.assertEquals(CharVar.of((char) 1).add(1).get(), 2);
        Assert.assertEquals(CharVar.of((char) 1).getAndIncrement(), 1);
        Assert.assertEquals(CharVar.of((char) 1).incrementAndGet(), 2);
        CharVar cv = CharVar.of((char) 1);
        Assert.assertEquals(cv.getAndIncrement(), 1);
        Assert.assertEquals(cv.get(), 2);
        Assert.assertEquals(CharVar.of((char) 1).toWrapper().get(), Character.valueOf((char) 1));

        Assert.assertNotEquals(ShortVar.ofZero(), ShortVar.ofZero());
        Assert.assertEquals(ShortVar.of((short) 1).get(), 1);
        Assert.assertEquals(ShortVar.of((short) 1).set((short) 2).get(), 2);
        Assert.assertEquals(ShortVar.of((short) 1).add(1).get(), 2);
        Assert.assertEquals(ShortVar.of((short) 1).getAndIncrement(), 1);
        Assert.assertEquals(ShortVar.of((short) 1).incrementAndGet(), 2);
        ShortVar sv = ShortVar.of((short) 1);
        Assert.assertEquals(sv.getAndIncrement(), 1);
        Assert.assertEquals(sv.get(), 2);
        Assert.assertEquals(ShortVar.of((short) 1).toWrapper().get(), Short.valueOf((short) 1));

        Assert.assertNotEquals(IntVar.ofZero(), IntVar.ofZero());
        Assert.assertEquals(IntVar.of(1).get(), 1);
        Assert.assertEquals(IntVar.of(1).set(2).get(), 2);
        Assert.assertEquals(IntVar.of(1).add(1).get(), 2);
        Assert.assertEquals(IntVar.of(1).getAndIncrement(), 1);
        Assert.assertEquals(IntVar.of(1).incrementAndGet(), 2);
        IntVar iv = IntVar.of(1);
        Assert.assertEquals(iv.getAndIncrement(), 1);
        Assert.assertEquals(iv.get(), 2);
        Assert.assertEquals(IntVar.of(1).toWrapper().get(), Integer.valueOf(1));

        Assert.assertNotEquals(LongVar.ofZero(), LongVar.ofZero());
        Assert.assertEquals(LongVar.of(1).get(), 1);
        Assert.assertEquals(LongVar.of(1).set(2).get(), 2);
        Assert.assertEquals(LongVar.of(1).add(1).get(), 2);
        Assert.assertEquals(LongVar.of(1).getAndIncrement(), 1);
        Assert.assertEquals(LongVar.of(1).incrementAndGet(), 2);
        LongVar lv = LongVar.of(1);
        Assert.assertEquals(lv.getAndIncrement(), 1);
        Assert.assertEquals(lv.get(), 2);
        Assert.assertEquals(LongVar.of(1).toWrapper().get(), Long.valueOf(1));

        Assert.assertNotEquals(FloatVar.ofZero(), FloatVar.ofZero());
        Assert.assertEquals(FloatVar.of(1).get(), 1);
        Assert.assertEquals(FloatVar.of(1).set(2).get(), 2);
        Assert.assertEquals(FloatVar.of(1).add(1).get(), 2);
        Assert.assertEquals(FloatVar.of(1).toWrapper().get(), Float.valueOf(1));

        Assert.assertNotEquals(DoubleVar.ofZero(), DoubleVar.ofZero());
        Assert.assertEquals(DoubleVar.of(1).get(), 1);
        Assert.assertEquals(DoubleVar.of(1).set(2).get(), 2);
        Assert.assertEquals(DoubleVar.of(1).add(1).get(), 2);
        Assert.assertEquals(DoubleVar.of(1).toWrapper().get(), Double.valueOf(1));
    }
}
