package test;

import com.google.common.base.CaseFormat;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.GekCase;
import xyz.fslabo.common.base.JieString;
import xyz.fslabo.common.coll.JieColl;

import java.util.Arrays;

public class CaseTest {

    @Test
    public void testCamelCase() {
        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("AaBbCcc", GekCase.LOWER_CAMEL), "aaBbCcc");
        Assert.assertEquals(GekCase.LOWER_CAMEL.toCase("aaBbCcc", GekCase.UPPER_CAMEL), "AaBbCcc");
        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("AABbCcc", GekCase.LOWER_CAMEL), "AABbCcc");
        Assert.assertEquals(GekCase.LOWER_CAMEL.toCase("AABbCcc", GekCase.UPPER_CAMEL), "AABbCcc");
        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("A0BbCcc", GekCase.LOWER_CAMEL), "a0BbCcc");
        Assert.assertEquals(GekCase.LOWER_CAMEL.toCase("a0BbCcc", GekCase.UPPER_CAMEL), "A0BbCcc");
        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("AaBbCCcc", GekCase.LOWER_CAMEL), "aaBbCCcc");
        Assert.assertEquals(GekCase.LOWER_CAMEL.toCase("aaBbCCcc", GekCase.UPPER_CAMEL), "AaBbCCcc");
        Assert.assertEquals(
            JieColl.toStringList(GekCase.UPPER_CAMEL.tokenize("AAAAABBBBBCCCCCcccccDDEe")),
            Arrays.asList("AAAAABBBBBCCCC", "Cccccc", "DD", "Ee")
        );

        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("AAA", GekCase.LOWER_CAMEL), "AAA");
        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("AAa", GekCase.LOWER_CAMEL), "aAa");
        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("aAa", GekCase.UPPER_CAMEL), "AAa");
        Assert.assertEquals(GekCase.LOWER_CAMEL.toCase("AAa", GekCase.LOWER_CAMEL), "aAa");
    }

    @Test
    public void testDelimiterCase() {
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.toCase("aa_bb_cc", GekCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.toCase("aa_bb_cc", GekCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(GekCase.UPPER_UNDERSCORE.toCase("AA_BB_CC", GekCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(GekCase.UPPER_UNDERSCORE.toCase("AA_BB_CC", GekCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.toCase("aa_bb_cc", GekCase.UPPER_HYPHEN), "AA-BB-CC");
        Assert.assertEquals(GekCase.UPPER_HYPHEN.toCase("AA-BB-CC", GekCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.toCase("aa_bb_cc_", GekCase.LOWER_HYPHEN), "aa-bb-cc-");
        Assert.assertEquals(GekCase.LOWER_HYPHEN.toCase("-aa-bb-cc-", GekCase.LOWER_UNDERSCORE), "_aa_bb_cc_");
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.toCase("AA_bb_cc", GekCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(GekCase.UPPER_UNDERSCORE.toCase("aa_BB_CC", GekCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(
            GekCase.delimiterCase("_", t -> JieString.upperCase(t.toChars())).toCase("aa_bb_cc",
                GekCase.delimiterCase("-", t -> JieString.upperCase(t.toChars()))), "AA-BB-CC");
        Assert.assertEquals(
            GekCase.delimiterCase("_", t -> JieString.lowerCase(t.toChars())).toCase("AA_BB_CC",
                GekCase.delimiterCase("-", t -> JieString.lowerCase(t.toChars()))), "aa-bb-cc");
        //Guava bug!
        // Assert.assertEquals(
        //     CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, "AA_BB_CC"),
        //     "aa_bb_cc");
        Assert.assertEquals(GekCase.UNDERSCORE.toCase("aa_bb_Cc", GekCase.HYPHEN), "aa-bb-Cc");
        Assert.assertEquals(GekCase.HYPHEN.toCase("aa-Bb-cc", GekCase.UNDERSCORE), "aa_Bb_cc");
    }

    @Test
    public void testMixCase() {
        Assert.assertEquals(GekCase.UPPER_UNDERSCORE.toCase("aa_bb_cc", GekCase.UPPER_CAMEL), "AaBbCc");
        Assert.assertEquals(GekCase.LOWER_HYPHEN.toCase("aa-bb-cc-", GekCase.LOWER_CAMEL), "aaBbCc");
        //has an empty part
        Assert.assertEquals(GekCase.UPPER_HYPHEN.toCase("-aa-bb-cc-", GekCase.LOWER_CAMEL), "AaBbCc");

        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("AaBbCcc", GekCase.LOWER_HYPHEN), "aa-bb-ccc");
        Assert.assertEquals(GekCase.UPPER_CAMEL.toCase("A0BbCcc", GekCase.UPPER_UNDERSCORE), "A0_BB_CCC");
        Assert.assertEquals(
            GekCase.UPPER_UNDERSCORE.toCase("get_Simple_Name_Options_Builder", GekCase.LOWER_CAMEL),
            "getSimpleNameOptionsBuilder");
        Assert.assertEquals(
            CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "get_Simple_Name_Options_Builder"),
            "getSimpleNameOptionsBuilder");
    }

    @Test
    public void testNumberCase() {
        GekCase case1 = GekCase.camelCase(true, GekCase.AS_INDEPENDENT);
        Assert.assertEquals(case1.toCase("aaBbCc00cC0", GekCase.LOWER_HYPHEN), "aa-bb-cc-00-c-c-0");
        GekCase case2 = GekCase.camelCase(true, GekCase.AS_INDEPENDENT);
        Assert.assertEquals(case2.toCase("aaBbCc00cC0", GekCase.LOWER_CAMEL), "aaBbCc00CC0");
        GekCase case3 = GekCase.camelCase(true, GekCase.AS_UPPER);
        Assert.assertEquals(case3.toCase("aaBbCc00cC0", GekCase.LOWER_CAMEL), "aaBbCc00cC0");
    }
}
