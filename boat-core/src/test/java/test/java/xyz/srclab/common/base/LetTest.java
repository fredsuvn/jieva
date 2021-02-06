package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Let;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author sunqian
 */
public class LetTest {

    @Test
    public void testLet() {
        Let<String> let = Let.of("1,2,3,4,5,6");
        int sum = let.then(s -> s.split(","))
                .then(Arrays::asList)
                .then(l -> l.stream().mapToInt(Integer::parseInt))
                .then(IntStream::sum)
                .get();
        Assert.assertEquals(sum, 21);
    }
}
