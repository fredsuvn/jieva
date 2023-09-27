package test;

import com.google.common.base.CaseFormat;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fs404.common.base.FsCase;
import xyz.fs404.common.collect.FsCollect;

import java.util.Arrays;

public class CaseTest {

    @Test
    public void testCamelCase() {
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AaBbCcc", FsCase.LOWER_CAMEL), "aaBbCcc");
        Assert.assertEquals(FsCase.LOWER_CAMEL.convert("aaBbCcc", FsCase.UPPER_CAMEL), "AaBbCcc");
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AABbCcc", FsCase.LOWER_CAMEL), "AABbCcc");
        Assert.assertEquals(FsCase.LOWER_CAMEL.convert("AABbCcc", FsCase.UPPER_CAMEL), "AABbCcc");
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("A0BbCcc", FsCase.LOWER_CAMEL), "a0BbCcc");
        Assert.assertEquals(FsCase.LOWER_CAMEL.convert("a0BbCcc", FsCase.UPPER_CAMEL), "A0BbCcc");
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AaBbCCcc", FsCase.LOWER_CAMEL), "aaBbCCcc");
        Assert.assertEquals(FsCase.LOWER_CAMEL.convert("aaBbCCcc", FsCase.UPPER_CAMEL), "AaBbCCcc");
        Assert.assertEquals(
            FsCollect.toStringList(FsCase.UPPER_CAMEL.split("AAAAABBBBBCCCCCcccccDDEe")),
            Arrays.asList("AAAAABBBBBCCCC", "Cccccc", "DD", "Ee")
        );

        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AAA", FsCase.LOWER_CAMEL), "AAA");
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AAa", FsCase.LOWER_CAMEL), "aAa");
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("aAa", FsCase.UPPER_CAMEL), "AAa");
        Assert.assertEquals(FsCase.LOWER_CAMEL.convert("AAa", FsCase.LOWER_CAMEL), "aAa");
    }

    @Test
    public void testSeparatorCase() {
        Assert.assertEquals(FsCase.LOWER_UNDERSCORE.convert("aa_bb_cc", FsCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(FsCase.LOWER_UNDERSCORE.convert("aa_bb_cc", FsCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(FsCase.UPPER_UNDERSCORE.convert("AA_BB_CC", FsCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(FsCase.UPPER_UNDERSCORE.convert("AA_BB_CC", FsCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(FsCase.LOWER_UNDERSCORE.convert("aa_bb_cc", FsCase.UPPER_HYPHEN), "AA-BB-CC");
        Assert.assertEquals(FsCase.UPPER_HYPHEN.convert("AA-BB-CC", FsCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(FsCase.LOWER_UNDERSCORE.convert("aa_bb_cc_", FsCase.LOWER_HYPHEN), "aa-bb-cc-");
        Assert.assertEquals(FsCase.LOWER_HYPHEN.convert("-aa-bb-cc-", FsCase.LOWER_UNDERSCORE), "_aa_bb_cc_");
        Assert.assertEquals(FsCase.LOWER_UNDERSCORE.convert("AA_bb_cc", FsCase.LOWER_UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(FsCase.UPPER_UNDERSCORE.convert("aa_BB_CC", FsCase.UPPER_UNDERSCORE), "AA_BB_CC");
        Assert.assertEquals(
            FsCase.separatorCase("_", true).convert("aa_bb_cc",
                FsCase.separatorCase("-", true)), "AA-BB-CC");
        Assert.assertEquals(
            FsCase.separatorCase("_", false).convert("AA_BB_CC",
                FsCase.separatorCase("-", false)), "aa-bb-cc");
        //Guava bug!
        // Assert.assertEquals(
        //     CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, "AA_BB_CC"),
        //     "aa_bb_cc");
    }

    @Test
    public void testMixCase() {
        Assert.assertEquals(FsCase.UPPER_UNDERSCORE.convert("aa_bb_cc", FsCase.UPPER_CAMEL), "AaBbCc");
        Assert.assertEquals(FsCase.LOWER_HYPHEN.convert("aa-bb-cc-", FsCase.LOWER_CAMEL), "aaBbCc");
        //has an empty part
        Assert.assertEquals(FsCase.UPPER_HYPHEN.convert("-aa-bb-cc-", FsCase.LOWER_CAMEL), "AaBbCc");

        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AaBbCcc", FsCase.LOWER_HYPHEN), "aa-bb-ccc");
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("A0BbCcc", FsCase.UPPER_UNDERSCORE), "A0_BB_CCC");
        Assert.assertEquals(
            FsCase.UPPER_UNDERSCORE.convert("get_Simple_Name_Options_Builder", FsCase.LOWER_CAMEL),
            "getSimpleNameOptionsBuilder");
        Assert.assertEquals(
            CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "get_Simple_Name_Options_Builder"),
            "getSimpleNameOptionsBuilder");
    }
}
