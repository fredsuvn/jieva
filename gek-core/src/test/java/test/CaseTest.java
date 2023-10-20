package test;

import com.google.common.base.CaseFormat;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekCase;
import xyz.fsgek.common.collect.GekColl;

import java.util.Arrays;

public class CaseTest {

    @Test
    public void testCamelCase() {
        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("AaBbCcc", GekCase.LOWER_CAMEL), "aaBbCcc");
        Assert.assertEquals(GekCase.LOWER_CAMEL.convert("aaBbCcc", GekCase.UPPER_CAMEL), "AaBbCcc");
        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("AABbCcc", GekCase.LOWER_CAMEL), "AABbCcc");
        Assert.assertEquals(GekCase.LOWER_CAMEL.convert("AABbCcc", GekCase.UPPER_CAMEL), "AABbCcc");
        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("A0BbCcc", GekCase.LOWER_CAMEL), "a0BbCcc");
        Assert.assertEquals(GekCase.LOWER_CAMEL.convert("a0BbCcc", GekCase.UPPER_CAMEL), "A0BbCcc");
        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("AaBbCCcc", GekCase.LOWER_CAMEL), "aaBbCCcc");
        Assert.assertEquals(GekCase.LOWER_CAMEL.convert("aaBbCCcc", GekCase.UPPER_CAMEL), "AaBbCCcc");
        Assert.assertEquals(
            GekColl.toStringList(GekCase.UPPER_CAMEL.split("AAAAABBBBBCCCCCcccccDDEe")),
            Arrays.asList("AAAAABBBBBCCCC", "Cccccc", "DD", "Ee")
        );

        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("AAA", GekCase.LOWER_CAMEL), "AAA");
        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("AAa", GekCase.LOWER_CAMEL), "aAa");
        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("aAa", GekCase.UPPER_CAMEL), "AAa");
        Assert.assertEquals(GekCase.LOWER_CAMEL.convert("AAa", GekCase.LOWER_CAMEL), "aAa");
    }

    @Test
    public void testSeparatorCase() {
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.convert("aa_bb_cc", GekCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.convert("aa_bb_cc", GekCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(GekCase.UPPER_UNDERSCORE.convert("AA_BB_CC", GekCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(GekCase.UPPER_UNDERSCORE.convert("AA_BB_CC", GekCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.convert("aa_bb_cc", GekCase.UPPER_HYPHEN), "AA-BB-CC");
        Assert.assertEquals(GekCase.UPPER_HYPHEN.convert("AA-BB-CC", GekCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.convert("aa_bb_cc_", GekCase.LOWER_HYPHEN), "aa-bb-cc-");
        Assert.assertEquals(GekCase.LOWER_HYPHEN.convert("-aa-bb-cc-", GekCase.LOWER_UNDERSCORE), "_aa_bb_cc_");
        Assert.assertEquals(GekCase.LOWER_UNDERSCORE.convert("AA_bb_cc", GekCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(GekCase.UPPER_UNDERSCORE.convert("aa_BB_CC", GekCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(
            GekCase.separatorCase("_", true).convert("aa_bb_cc",
                GekCase.separatorCase("-", true)), "AA-BB-CC");
        Assert.assertEquals(
            GekCase.separatorCase("_", false).convert("AA_BB_CC",
                GekCase.separatorCase("-", false)), "aa-bb-cc");
        //Guava bug!
        // Assert.assertEquals(
        //     CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, "AA_BB_CC"),
        //     "aa_bb_cc");
    }

    @Test
    public void testMixCase() {
        Assert.assertEquals(GekCase.UPPER_UNDERSCORE.convert("aa_bb_cc", GekCase.UPPER_CAMEL), "AaBbCc");
        Assert.assertEquals(GekCase.LOWER_HYPHEN.convert("aa-bb-cc-", GekCase.LOWER_CAMEL), "aaBbCc");
        //has an empty part
        Assert.assertEquals(GekCase.UPPER_HYPHEN.convert("-aa-bb-cc-", GekCase.LOWER_CAMEL), "AaBbCc");

        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("AaBbCcc", GekCase.LOWER_HYPHEN), "aa-bb-ccc");
        Assert.assertEquals(GekCase.UPPER_CAMEL.convert("A0BbCcc", GekCase.UPPER_UNDERSCORE), "A0_BB_CCC");
        Assert.assertEquals(
            GekCase.UPPER_UNDERSCORE.convert("get_Simple_Name_Options_Builder", GekCase.LOWER_CAMEL),
            "getSimpleNameOptionsBuilder");
        Assert.assertEquals(
            CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "get_Simple_Name_Options_Builder"),
            "getSimpleNameOptionsBuilder");
    }
}
