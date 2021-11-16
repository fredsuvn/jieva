package sample.java.xyz.srclab.core.collect;

import org.testng.annotations.Test;
import xyz.srclab.common.collect.*;
import xyz.srclab.common.lang.Nums;
import xyz.srclab.common.test.TestLogger;

import java.util.*;

public class CollectSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testArray() {
        String[] strings = Collects.newArray("1", "2", "3");
        Collects.asList(strings).set(0, "111");
        //111
        logger.log("string[0]: {}", strings[0]);
    }

    @Test
    public void testCollect() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        Collecting<String> collect = Collects.collecting(list);
        int sum = collect.addAll(Collects.newArray("4", "5", "6"))
            .removeFirst()
            .map(it -> it + "0")
            .map(Nums::toInt)
            .reduce(Integer::sum);
        //200
        logger.log("sum: {}", sum);
    }

    @Test
    public void testMultiMap() {
        BSetMap<String, String> setMap = BSetMap.newSetMap(
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedHashSet<>(), "1", "2", "3")
            )
        );
        //setMap: {s=[1, 2, 3]}
        logger.log("setMap: {}", setMap);

        MutableSetMap<String, String> mutableSetMap = MutableSetMap.newMutableSetMap(
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedHashSet<>(), "1", "2", "3")
            )
        );
        mutableSetMap.add("s", "9");
        mutableSetMap.addAll("s", Collects.newCollection(new LinkedHashSet<>(), "11", "12", "13"));
        //mutableSetMap: {s=[1, 2, 3, 9, 11, 12, 13]}
        logger.log("mutableSetMap: {}", mutableSetMap);

        BListMap<String, String> listMap = BListMap.newListMap(
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedList<>(), "1", "2", "3")
            )
        );
        //listMap: {s=[1, 2, 3]}
        logger.log("listMap: {}", listMap);

        MutableListMap<String, String> mutableListMap = MutableListMap.newMutableListMap(
            Collects.newMap(
                new LinkedHashMap<>(),
                "s", Collects.newCollection(new LinkedList<>(), "1", "2", "3")
            )
        );
        mutableListMap.add("s", "9");
        mutableListMap.addAll("s", Collects.newCollection(new LinkedList<>(), "11", "12", "13"));
        //mutableListMap: {s=[1, 2, 3, 9, 11, 12, 13]}
        logger.log("mutableListMap: {}", mutableListMap);
    }
}
