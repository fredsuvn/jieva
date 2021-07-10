package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.*;
import xyz.srclab.common.lang.Nums;
import xyz.srclab.common.test.TestLogger;

import java.util.*;

/**
 * @author sunqian
 */
public class CollectsTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testArray() {
        String[] stringArray = Collects.newArray("1", "2", "3");
        logger.log(Collects.joinToString(stringArray));
        Assert.assertEquals(
            Collects.joinToString(stringArray),
            "1, 2, 3"
        );
    }

    @Test
    public void testList() {
        String[] strings = Collects.newArray("1", "2", "3");
        Collects.asList(strings).set(0, "111");
        Collects.asList(strings).set(1, "222");
        Collects.asList(strings).set(2, "333");
        Assert.assertEquals(
            Collects.joinToString(strings),
            "111, 222, 333"
        );
        Assert.assertEquals(
            Collects.joinToString(Collects.asList(strings)),
            "111, 222, 333"
        );

        int[] ints = {1, 2, 3};
        Collects.asList(ints).set(0, 111);
        Collects.asList(ints).set(1, 222);
        Collects.asList(ints).set(2, 333);
        Assert.assertEquals(
            Collects.joinToString(ints),
            "111, 222, 333"
        );
        Assert.assertEquals(
            Collects.joinToString(Collects.asList(ints)),
            "111, 222, 333"
        );
    }

    @Test
    public void testCollecting() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        Collecting<String> collecting = Collects.collect(list);
        int sum = collecting.addAll(Collects.newArray("4", "5", "6"))
            .removeFirst()
            .map(it -> it + "0")
            .map(Nums::toInt)
            .reduce(Integer::sum);
        Assert.assertEquals(sum, 200);

        int[] ints = {1, 2, 3};
        Collecting<Integer> collecting2 = Collects.collect(Collects.asList(ints));
        int sum2 = collecting2.reduce(Integer::sum);
        Assert.assertEquals(sum2, 6);
        Assert.expectThrows(UnsupportedOperationException.class, () -> {
            collecting2.addAll(Arrays.asList(4, 5, 6));
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

    @Test
    public void testCopyOnWriteMap() {
        Map<String, Object> map = new CopyOnWriteMap<>();
        Map<String, Object> hashMap = new HashMap<>();
        for (int i = 0; i <= 10086; i++) {
            String key = String.valueOf(i);
            map.put(key, key);
            hashMap.put(key, key);
        }
        for (int i = 0; i <= 10086; i++) {
            String key = String.valueOf(i);
            Object value = map.get(key);
            Object hashValue = hashMap.get(key);
            Assert.assertEquals(hashValue, value);
        }
    }
}
