package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BCase;
import xyz.srclab.common.base.CamelCase;

public class BCaseTest {

    @Test
    public void testNamingCase() {
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("UpperCamel", BCase.LOWER_CAMEL),
            "upperCamel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("UpperCamel", BCase.LOWER_HYPHEN),
            "upper-camel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("UpperCamel", BCase.LOWER_UNDERSCORE),
            "upper_camel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("AUpperCamel", BCase.LOWER_CAMEL),
            "aUpperCamel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("AUpperCamel", BCase.LOWER_HYPHEN),
            "a-upper-camel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("AUpperCamel", BCase.LOWER_UNDERSCORE),
            "a_upper_camel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("upperCamel", BCase.LOWER_CAMEL),
            "upperCamel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("upperCamel", BCase.LOWER_HYPHEN),
            "upper-camel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("upperCamel", BCase.LOWER_UNDERSCORE),
            "upper_camel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("upper2Camel", BCase.LOWER_UNDERSCORE),
            "upper2_camel"
        );
        Assert.assertEquals(
            BCase.lowerCamelCase(CamelCase.NonLetterPolicy.AS_LOWER).convert("upper2Camel", BCase.LOWER_UNDERSCORE),
            "upper2_camel"
        );
        Assert.assertEquals(
            BCase.lowerCamelCase(CamelCase.NonLetterPolicy.AS_UPPER).convert("upper2camel", BCase.LOWER_UNDERSCORE),
            "upper_2camel"
        );
        Assert.assertEquals(
            BCase.lowerCamelCase(CamelCase.NonLetterPolicy.AS_UPPER).convert("upper2Camel", BCase.LOWER_UNDERSCORE),
            "upper_2_camel"
        );
        Assert.assertEquals(
            BCase.lowerCamelCase(CamelCase.NonLetterPolicy.FOLLOWER_STARTS_WITH_LOWER).convert("2upper2Camel", BCase.LOWER_UNDERSCORE),
            "2upper2_camel"
        );
        Assert.assertEquals(
            BCase.lowerCamelCase(CamelCase.NonLetterPolicy.FOLLOWER_STARTS_WITH_UPPER).convert("2upper2Camel", BCase.LOWER_UNDERSCORE),
            "2upper2_camel"
        );
        Assert.assertEquals(
            BCase.UPPER_CAMEL.convert("upper@#$%Camel", BCase.LOWER_UNDERSCORE),
            "upper@#$%_camel"
        );
    }
}
