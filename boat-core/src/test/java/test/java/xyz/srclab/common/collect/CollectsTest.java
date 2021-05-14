package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Nums;
import xyz.srclab.common.collect.*;
import xyz.srclab.common.test.TestLogger;

import java.util.*;

/**
 * @author sunqian
 */
public class CollectsTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testArray() {
        String[] stringArray = ArrayCollects.newArray("1", "2", "3");
        logger.log(ArrayCollects.joinToString(stringArray));
        Assert.assertEquals(
            ArrayCollects.joinToString(stringArray),
            "1, 2, 3"
        );
    }

    @Test
    public void testList() {
        String[] strings = ArrayCollects.newArray("1", "2", "3");
        ArrayCollects.asList(strings).set(0, "111");
        ArrayCollects.asList(strings).set(1, "222");
        ArrayCollects.asList(strings).set(2, "333");
        Assert.assertEquals(
            ArrayCollects.joinToString(strings),
            "111, 222, 333"
        );
        Assert.assertEquals(
            Collects.joinToString(ArrayCollects.asList(strings)),
            "111, 222, 333"
        );

        int[] ints = {1, 2, 3};
        ArrayCollects.asList(ints).set(0, 111);
        ArrayCollects.asList(ints).set(1, 222);
        ArrayCollects.asList(ints).set(2, 333);
        Assert.assertEquals(
            ArrayCollects.joinToString(ints),
            "111, 222, 333"
        );
        Assert.assertEquals(
            Collects.joinToString(ArrayCollects.asList(ints)),
            "111, 222, 333"
        );
    }

    @Test
    public void testOps() {
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
        Assert.assertEquals(sum, 200);

        int[] ints = {1, 2, 3};
        ListOps<Integer> listOps2 = ListOps.opsFor(ArrayCollects.asList(ints));
        int sum2 = listOps2.reduce(Integer::sum);
        Assert.assertEquals(sum2, 6);
        Assert.expectThrows(UnsupportedOperationException.class, () -> {
            listOps2.addAll(Arrays.asList(4, 5, 6));
        });
    }

    @Test
    public void testMultiMap() {
        //Set
        SetMap<String, String> setMap = SetMap.newSetMap(
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedHashSet<>(), "1", "2", "3")
            )
        );
        logger.log("setMap: {}", setMap);
        Assert.assertEquals(
            setMap,
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedHashSet<>(), "1", "2", "3")
            )
        );
        MutableSetMap<String, String> mutableSetMap = MutableSetMap.newMutableSetMap(
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedHashSet<>(), "1", "2", "3")
            )
        );
        mutableSetMap.add("s", "9");
        mutableSetMap.addAll("s", Collects.newCollection(new LinkedHashSet<>(), "11", "12", "13"));
        logger.log("mutableSetMap: {}", mutableSetMap);
        Assert.assertEquals(
            mutableSetMap,
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(
                    new LinkedHashSet<>(), "1", "2", "3", "9", "11", "12", "13")
            )
        );

        //List
        ListMap<String, String> listMap = ListMap.newListMap(
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedList<>(), "1", "2", "3")
            )
        );
        logger.log("listMap: {}", listMap);
        Assert.assertEquals(
            listMap,
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedList<>(), "1", "2", "3")
            )
        );
        MutableListMap<String, String> mutableListMap = MutableListMap.newMutableListMap(
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedList<>(), "1", "2", "3")
            )
        );
        mutableListMap.add("s", "9");
        mutableListMap.addAll("s", Collects.newCollection(new LinkedList<>(), "11", "12", "13"));
        logger.log("mutableListMap: {}", mutableListMap);
        Assert.assertEquals(
            mutableListMap,
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(
                    new LinkedList<>(), "1", "2", "3", "9", "11", "12", "13")
            )
        );
    }
}
