package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.NamingCase;

public class NamingCaseTest {

    @Test
    public void testNamingCase() {
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("UpperCamel", NamingCase.LOWER_CAMEL),
            "upperCamel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("UpperCamel", NamingCase.LOWER_HYPHEN),
            "upper-camel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("UpperCamel", NamingCase.LOWER_UNDERSCORE),
            "upper_camel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("AUpperCamel", NamingCase.LOWER_CAMEL),
            "aUpperCamel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("AUpperCamel", NamingCase.LOWER_HYPHEN),
            "a-upper-camel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("AUpperCamel", NamingCase.LOWER_UNDERSCORE),
            "a_upper_camel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("upperCamel", NamingCase.LOWER_CAMEL),
            "upperCamel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("upperCamel", NamingCase.LOWER_HYPHEN),
            "upper-camel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("upperCamel", NamingCase.LOWER_UNDERSCORE),
            "upper_camel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("upper2Camel", NamingCase.LOWER_UNDERSCORE),
            "upper2_camel"
        );
        Assert.assertEquals(
            NamingCase.UPPER_CAMEL.convert("upper@#$%Camel", NamingCase.LOWER_UNDERSCORE),
            "upper_camel"
        );
    }
}
