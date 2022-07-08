package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtCase;
import xyz.srclab.common.base.BtString;
import xyz.srclab.common.base.CamelCase;
import xyz.srclab.common.base.NamingCase;

public class BtCaseTest {

    private static final NamingCase asUpperLowerCamel = new CamelCase(true, CamelCase.NonLetterPolicy.AS_UPPER);
    private static final NamingCase separateLowerCamel = new CamelCase(true, CamelCase.NonLetterPolicy.INDEPENDENT);
    private static final NamingCase toLowerUpperCamel = new CamelCase(true, (i, s) -> BtString.lowerCase(s));

    @Test
    public void testCase() {
        Assert.assertEquals(
            BtCase.toCase("AaBb", BtCase.upperCamel(), BtCase.lowerCamel()),
            "aaBb"
        );
        Assert.assertEquals(
            BtCase.toCase("AAaBb00a", BtCase.upperCamel(), BtCase.lowerCamel()),
            "aAaBb00a"
        );
        Assert.assertEquals(
            BtCase.toCase("AAAaBb00a", BtCase.upperCamel(), BtCase.lowerCamel()),
            "AAAaBb00a"
        );
        Assert.assertEquals(
            BtCase.toCase("aaAaAa", BtCase.upperCamel(), BtCase.upperCamel()),
            "AaAaAa"
        );
        Assert.assertEquals(
            BtCase.toCase("URLAddress", BtCase.lowerCamel(), BtCase.lowerCamel()),
            "URLAddress"
        );
        Assert.assertEquals(
            BtCase.toCase("URLAddress", BtCase.lowerCamel(), BtCase.lowerUnderscore()),
            "url_address"
        );
        Assert.assertEquals(
            BtCase.toCase("IDAddress", BtCase.lowerCamel(), BtCase.lowerUnderscore()),
            "id_address"
        );
        Assert.assertEquals(
            BtCase.toCase("XAddress", BtCase.lowerCamel(), BtCase.lowerUnderscore()),
            "x_address"
        );
        Assert.assertEquals(
            BtCase.toCase("XAddress", BtCase.lowerCamel(), BtCase.lowerCamel()),
            "xAddress"
        );
        Assert.assertEquals(
            BtCase.toCase("AAA000aBb00a0", asUpperLowerCamel, BtCase.lowerUnderscore()),
            "aaa00_0a_bb_0_0a_0"
        );
        Assert.assertEquals(
            BtCase.toCase("AAA000aBBb00a", separateLowerCamel, BtCase.lowerUnderscore()),
            "aaa_000_a_b_bb_00_a"
        );
        Assert.assertEquals(
            BtCase.toCase("0Address", asUpperLowerCamel, BtCase.lowerUnderscore()),
            "0_address"
        );
        Assert.assertEquals(
            BtCase.toCase("0address", asUpperLowerCamel, BtCase.lowerUnderscore()),
            "0address"
        );
        Assert.assertEquals(
            BtCase.toCase("idA0123", BtCase.lowerCamel(), BtCase.lowerUnderscore()),
            "id_a0123"
        );
        Assert.assertEquals(
            BtCase.toCase("AA_BB", BtCase.upperUnderscore(), toLowerUpperCamel),
            "AaBb"
        );
    }
}
