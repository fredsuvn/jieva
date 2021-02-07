package sample.xyz.srclab.common.collect;

import org.testng.annotations.Test;
import xyz.srclab.common.base.Nums;
import xyz.srclab.common.collect.ArrayCollects;
import xyz.srclab.common.collect.ListOps;
import xyz.srclab.common.test.TestLogger;

import java.util.ArrayList;
import java.util.List;

public class CollectSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testArray() {
        String[] strings = ArrayCollects.newArray("1", "2", "3");
        ArrayCollects.asList(strings).set(0, "111");
        //111
        logger.log("string[0]: {}", strings[0]);
    }

    @Test
    public void testCollect() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        ListOps<String> listOps = ListOps.opsFor(list);
        int sum = listOps.addAll(ArrayCollects.newArray("4", "5", "6"))
                .removeFirst()
                .map(it -> it + "0")
                .map(Nums::toInt)
                .reduce(Integer::sum);
        //200
        logger.log("sum: {}", sum);
    }
}
