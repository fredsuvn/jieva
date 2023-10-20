package test;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.collect.GekColl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringTest {

    @Test
    public void testConcat() {
        List<String> list = Arrays.asList(
            "dsfasfas",
            "fsafs",
            "fasdf",
            "fas",
            "fdfsf",
            "fsafsaf"
        );
        Assert.assertEquals(
            GekString.concat(list.toArray()),
            String.join("", list)
        );
        Assert.assertEquals(
            GekString.concat(list),
            String.join("", list)
        );
    }

    @Test
    public void testStartWith() {
        String a = "123abc123";
        Assert.assertEquals(
            GekString.startWith(a, "456"),
            "456" + a
        );
        Assert.assertEquals(
            GekString.startWith(a, "123"),
            a
        );
        Assert.assertEquals(
            GekString.endWith(a, "456"),
            a + "456"
        );
        Assert.assertEquals(
            GekString.endWith(a, "123"),
            a
        );
        Assert.assertEquals(
            GekString.removeStart(a, "456"),
            a
        );
        Assert.assertEquals(
            GekString.removeStart(a, "123"),
            "abc123"
        );
        Assert.assertEquals(
            GekString.removeEnd(a, "456"),
            a
        );
        Assert.assertEquals(
            GekString.removeEnd(a, "123"),
            "123abc"
        );
    }

    @Test
    public void testIndexOf() {
        Assert.assertEquals(
            GekString.indexOf("1234567890", "2"),
            1
        );
        Assert.assertEquals(
            GekString.lastIndexOf("1234567890", "2"),
            1
        );
        Assert.assertEquals(
            GekString.indexOf("12345678901234567890", "2", 9),
            11
        );
        Assert.assertEquals(
            GekString.lastIndexOf("12345678901234567890", "2", 9),
            1
        );
        Assert.assertEquals(
            GekString.indexOf("11", "11"),
            0
        );
        Assert.assertEquals(
            GekString.lastIndexOf("11", "11"),
            0
        );
        Assert.assertEquals(
            GekString.indexOf("", ""),
            -1
        );
        Assert.assertEquals(
            GekString.indexOf("1", "11"),
            -1
        );
        Assert.assertEquals(
            GekString.lastIndexOf("", ""),
            -1
        );
        Assert.assertEquals(
            GekString.lastIndexOf("1", "11"),
            -1
        );
    }

    @Test
    public void testSplit() {
        Assert.assertEquals(
            GekColl.toStringList(GekString.split("123--123--123--", "--")),
            Arrays.asList("123", "123", "123", "")
        );
        Assert.assertEquals(
            GekColl.toStringList(GekString.split("123", "1234")),
            Collections.emptyList()
        );
        Assert.assertEquals(
            GekColl.toStringList(GekString.split("", "1234")),
            Collections.emptyList()
        );
        Assert.assertEquals(
            GekColl.toStringList(GekString.split("123", "123")),
            Arrays.asList("", "")
        );
        Assert.assertEquals(
            GekColl.toStringList(GekString.split("123--123--123----", "--")),
            Arrays.asList("123", "123", "123", "", "")
        );
        Assert.assertEquals(
            GekColl.toStringList(GekString.split("--123--123--123----", "--")),
            Arrays.asList("", "123", "123", "123", "", "")
        );
    }

    @Test
    public void testReplace() {
        Assert.assertEquals(
            GekString.replace("123--123--123--", "--", "66"),
            "123661236612366"
        );
        Assert.assertEquals(
            GekString.replace("----123--123--123----", "--", "+++"),
            "++++++123+++123+++123++++++"
        );
        Assert.assertEquals(
            GekString.replace("-----123--123---123----", "--", "+++"),
            "++++++-123+++123+++-123++++++"
        );
        Assert.assertEquals(
            GekString.replace("-----123--123---123----", "--", ""),
            "-123123-123"
        );
    }

    @Test
    public void testSubRef() {
        Assert.assertEquals(
            GekString.subView("12345678", 2, 7).toString(),
            "34567"
        );
        Assert.assertEquals(
            GekString.subView("12345678", 2, 7).charAt(3),
            '6'
        );
        Assert.assertEquals(
            GekString.subView("12345678", 2, 7).subSequence(1, 3).toString(),
            "45"
        );
    }

    @Test
    public void testEqual() {
        Assert.assertTrue(
            GekString.charEquals("123", "123")
        );
        Assert.assertTrue(
            GekString.charEquals("123", "1123".substring(1))
        );
        Assert.assertFalse(
            GekString.charEquals("1234", "123")
        );
        Assert.assertTrue(
            GekString.charEquals("123", new CharSequence() {
                @Override
                public int length() {
                    return 3;
                }

                @Override
                public char charAt(int index) {
                    if (index == 0) {
                        return '1';
                    }
                    if (index == 1) {
                        return '2';
                    }
                    if (index == 2) {
                        return '3';
                    }
                    return ' ';
                }

                @NotNull
                @Override
                public CharSequence subSequence(int start, int end) {
                    return null;
                }
            })
        );
    }

    @Test
    public void testCapitalize() {
        Assert.assertEquals("Abc", GekString.capitalize("abc"));
        Assert.assertEquals("Abc", GekString.capitalize("Abc"));
        Assert.assertEquals("abc", GekString.uncapitalize("abc"));
        Assert.assertEquals("abc", GekString.uncapitalize("Abc"));
    }

    @Test
    public void testLazyString() {
        int[] is = {0};
        CharSequence lazy = GekString.lazyString(() -> {
            is[0] = 1;
            return "8899";
        });
        Assert.assertEquals(is[0], 0);
        Assert.assertEquals(lazy.toString(), "8899");
        Assert.assertEquals(is[0], 1);
    }
}
