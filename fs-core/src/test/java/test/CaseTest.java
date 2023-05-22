package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsCase;

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
            FsCase.UPPER_CAMEL.split("AAAAABBBBBCCCCCcccccDDEe"),
            Arrays.asList("AAAAABBBBBCCCC", "Cccccc", "DD", "Ee")
        );

        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AAA", FsCase.LOWER_CAMEL), "AAA");
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AAa", FsCase.LOWER_CAMEL), "aAa");
    }

    @Test
    public void testSeparatorCase() {
        Assert.assertEquals(FsCase.UNDERSCORE.convert("aa_bb_cc", FsCase.HYPHEN), "aa-bb-cc");
        Assert.assertEquals(FsCase.HYPHEN.convert("aa-bb-cc", FsCase.UNDERSCORE), "aa_bb_cc");
        Assert.assertEquals(FsCase.UNDERSCORE.convert("aa_bb_cc_", FsCase.HYPHEN), "aa-bb-cc-");
        Assert.assertEquals(FsCase.HYPHEN.convert("-aa-bb-cc-", FsCase.UNDERSCORE), "_aa_bb_cc_");
        Assert.assertEquals(
            FsCase.separatorCase("_", true).convert("aa_bb_cc",
                FsCase.separatorCase("-", true)), "AA-BB-CC");
        Assert.assertEquals(
            FsCase.separatorCase("_", false).convert("AA_BB_CC",
                FsCase.separatorCase("-", false)), "aa-bb-cc");
    }

    @Test
    public void testMixCase() {
        Assert.assertEquals(FsCase.UNDERSCORE.convert("aa_bb_cc", FsCase.UPPER_CAMEL), "AaBbCc");
        Assert.assertEquals(FsCase.HYPHEN.convert("aa-bb-cc-", FsCase.LOWER_CAMEL), "aaBbCc");
        //has a empty part
        Assert.assertEquals(FsCase.HYPHEN.convert("-aa-bb-cc-", FsCase.LOWER_CAMEL), "AaBbCc");

        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("AaBbCcc", FsCase.HYPHEN), "Aa-Bb-Ccc");
        Assert.assertEquals(FsCase.UPPER_CAMEL.convert("A0BbCcc", FsCase.UNDERSCORE), "A0_Bb_Ccc");
    }
}
