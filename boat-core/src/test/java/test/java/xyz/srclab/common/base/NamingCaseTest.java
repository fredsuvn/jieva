package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BNamingCase;

public class NamingCaseTest {

    @Test
    public void testNamingCase() {
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("UpperCamel", BNamingCase.LOWER_CAMEL),
            "upperCamel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("UpperCamel", BNamingCase.LOWER_HYPHEN),
            "upper-camel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("UpperCamel", BNamingCase.LOWER_UNDERSCORE),
            "upper_camel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("AUpperCamel", BNamingCase.LOWER_CAMEL),
            "aUpperCamel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("AUpperCamel", BNamingCase.LOWER_HYPHEN),
            "a-upper-camel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("AUpperCamel", BNamingCase.LOWER_UNDERSCORE),
            "a_upper_camel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("upperCamel", BNamingCase.LOWER_CAMEL),
            "upperCamel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("upperCamel", BNamingCase.LOWER_HYPHEN),
            "upper-camel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("upperCamel", BNamingCase.LOWER_UNDERSCORE),
            "upper_camel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("upper2Camel", BNamingCase.LOWER_UNDERSCORE),
            "upper2_camel"
        );
        Assert.assertEquals(
            BNamingCase.UPPER_CAMEL.convert("upper@#$%Camel", BNamingCase.LOWER_UNDERSCORE),
            "upper_camel"
        );
    }
}
