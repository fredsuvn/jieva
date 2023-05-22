package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsCase;

public class CaseTest {

    @Test
    public void testCamelCase() {
        Assert.assertEquals(FsCase.UPPER_CASE.convert("AaBbCcc", FsCase.LOWER_CASE), "aaBbCcc");
        Assert.assertEquals(FsCase.LOWER_CASE.convert("aaBbCcc", FsCase.UPPER_CASE), "AaBbCcc");
        Assert.assertEquals(FsCase.UPPER_CASE.convert("AABbCcc", FsCase.LOWER_CASE), "AABbCcc");
        Assert.assertEquals(FsCase.LOWER_CASE.convert("AABbCcc", FsCase.UPPER_CASE), "AABbCcc");
        Assert.assertEquals(FsCase.UPPER_CASE.convert("A0BbCcc", FsCase.LOWER_CASE), "a0BbCcc");
        Assert.assertEquals(FsCase.LOWER_CASE.convert("a0BbCcc", FsCase.UPPER_CASE), "A0BbCcc");
    }

    @Test
    public void testSeparatorCase() {
        Assert.assertEquals(FsCase.UNDERLYING_CASE.convert("aa_bb_cc", FsCase.HYPHEN_CASE), "aa-bb-cc");
        Assert.assertEquals(FsCase.HYPHEN_CASE.convert("aa-bb-cc", FsCase.UNDERLYING_CASE), "aa_bb_cc");
        Assert.assertEquals(FsCase.UNDERLYING_CASE.convert("aa_bb_cc_", FsCase.HYPHEN_CASE), "aa-bb-cc-");
        Assert.assertEquals(FsCase.HYPHEN_CASE.convert("-aa-bb-cc-", FsCase.UNDERLYING_CASE), "_aa_bb_cc_");
        Assert.assertEquals(
            FsCase.separatorCase("_", true).convert("aa_bb_cc",
                FsCase.separatorCase("-", true)), "AA-BB-CC");
        Assert.assertEquals(
            FsCase.separatorCase("_", false).convert("AA_BB_CC",
                FsCase.separatorCase("-", false)), "aa-bb-cc");
    }

    @Test
    public void testMixCase() {
        Assert.assertEquals(FsCase.UNDERLYING_CASE.convert("aa_bb_cc", FsCase.UPPER_CASE), "AaBbCc");
        Assert.assertEquals(FsCase.HYPHEN_CASE.convert("aa-bb-cc-", FsCase.LOWER_CASE), "aaBbCc");
        //has a empty part
        Assert.assertEquals(FsCase.HYPHEN_CASE.convert("-aa-bb-cc-", FsCase.LOWER_CASE), "AaBbCc");
    }
}
