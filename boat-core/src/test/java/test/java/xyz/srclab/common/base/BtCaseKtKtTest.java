package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;

public class BtCaseKtKtTest {

    private static final NamingCase asUpperLowerCamel = new CamelCase(true, CamelCase.NonLetterPolicy.AS_UPPER);
    private static final NamingCase separateLowerCamel = new CamelCase(true, CamelCase.NonLetterPolicy.INDEPENDENT);
    private static final NamingCase toLowerUpperCamel = new CamelCase(true, BString::lowerCase);

    @Test
    public void testCase() {
        Assert.assertEquals(
            BCase.toCase("AaBb", BCase.upperCamel(), BCase.lowerCamel()),
            "aaBb"
        );
        Assert.assertEquals(
            BCase.toCase("AAaBb00a", BCase.upperCamel(), BCase.lowerCamel()),
            "aAaBb00a"
        );
        Assert.assertEquals(
            BCase.toCase("AAAaBb00a", BCase.upperCamel(), BCase.lowerCamel()),
            "AAAaBb00a"
        );
        Assert.assertEquals(
            BCase.toCase("aaAaAa", BCase.upperCamel(), BCase.upperCamel()),
            "AaAaAa"
        );
        Assert.assertEquals(
            BCase.toCase("URLAddress", BCase.lowerCamel(), BCase.lowerCamel()),
            "URLAddress"
        );
        Assert.assertEquals(
            BCase.toCase("URLAddress", BCase.lowerCamel(), BCase.lowerUnderscore()),
            "url_address"
        );
        Assert.assertEquals(
            BCase.toCase("IDAddress", BCase.lowerCamel(), BCase.lowerUnderscore()),
            "id_address"
        );
        Assert.assertEquals(
            BCase.toCase("XAddress", BCase.lowerCamel(), BCase.lowerUnderscore()),
            "x_address"
        );
        Assert.assertEquals(
            BCase.toCase("XAddress", BCase.lowerCamel(), BCase.lowerCamel()),
            "xAddress"
        );
        Assert.assertEquals(
            BCase.toCase("AAA000aBb00a0", asUpperLowerCamel, BCase.lowerUnderscore()),
            "aaa00_0a_bb_0_0a_0"
        );
        Assert.assertEquals(
            BCase.toCase("AAA000aBBb00a", separateLowerCamel, BCase.lowerUnderscore()),
            "aaa_000_a_b_bb_00_a"
        );
        Assert.assertEquals(
            BCase.toCase("0Address", asUpperLowerCamel, BCase.lowerUnderscore()),
            "0_address"
        );
        Assert.assertEquals(
            BCase.toCase("0address", asUpperLowerCamel, BCase.lowerUnderscore()),
            "0address"
        );
        Assert.assertEquals(
            BCase.toCase("idA0123", BCase.lowerCamel(), BCase.lowerUnderscore()),
            "id_a0123"
        );
        Assert.assertEquals(
            BCase.toCase("AA_BB", BCase.upperUnderscore(), toLowerUpperCamel),
            "AaBb"
        );
    }
}
