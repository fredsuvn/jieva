package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.NamingCase;

public class NamingCaseTest {

    @Test
    public void testNamingCase() {
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("UpperCamel", NamingCase.LOWER_CAMEL),
                "upperCamel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("UpperCamel", NamingCase.LOWER_HYPHEN),
                "upper-camel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("UpperCamel", NamingCase.LOWER_UNDERSCORE),
                "upper_camel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("AUpperCamel", NamingCase.LOWER_CAMEL),
                "aUpperCamel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("AUpperCamel", NamingCase.LOWER_HYPHEN),
                "a-upper-camel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("AUpperCamel", NamingCase.LOWER_UNDERSCORE),
                "a_upper_camel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("upperCamel", NamingCase.LOWER_CAMEL),
                "upperCamel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("upperCamel", NamingCase.LOWER_HYPHEN),
                "upper-camel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("upperCamel", NamingCase.LOWER_UNDERSCORE),
                "upper_camel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("upper2Camel", NamingCase.LOWER_UNDERSCORE),
                "upper2_camel"
        );
        Assert.assertEquals(
                NamingCase.UPPER_CAMEL.convertTo("upper@#$%Camel", NamingCase.LOWER_UNDERSCORE),
                "upper_camel"
        );
    }
}
