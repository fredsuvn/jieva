package test.coll;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.coll.JieArray;

import java.util.List;

public class ArrayTest {

    @Test
    public void testEmpty() {
        // Test with null arrays
        Assert.assertTrue(JieArray.isEmpty((Object[]) null));
        Assert.assertTrue(JieArray.isEmpty((boolean[]) null));
        Assert.assertTrue(JieArray.isEmpty((byte[]) null));
        Assert.assertTrue(JieArray.isEmpty((short[]) null));
        Assert.assertTrue(JieArray.isEmpty((char[]) null));
        Assert.assertTrue(JieArray.isEmpty((int[]) null));
        Assert.assertTrue(JieArray.isEmpty((long[]) null));
        Assert.assertTrue(JieArray.isEmpty((float[]) null));
        Assert.assertTrue(JieArray.isEmpty((double[]) null));

        // Test with empty arrays
        Assert.assertTrue(JieArray.isEmpty(new Object[0]));
        Assert.assertTrue(JieArray.isEmpty(new boolean[0]));
        Assert.assertTrue(JieArray.isEmpty(new byte[0]));
        Assert.assertTrue(JieArray.isEmpty(new short[0]));
        Assert.assertTrue(JieArray.isEmpty(new char[0]));
        Assert.assertTrue(JieArray.isEmpty(new int[0]));
        Assert.assertTrue(JieArray.isEmpty(new long[0]));
        Assert.assertTrue(JieArray.isEmpty(new float[0]));
        Assert.assertTrue(JieArray.isEmpty(new double[0]));

        // Test with non-empty arrays
        Assert.assertFalse(JieArray.isEmpty(new Object[]{1}));
        Assert.assertFalse(JieArray.isEmpty(new boolean[]{true}));
        Assert.assertFalse(JieArray.isEmpty(new byte[]{1}));
        Assert.assertFalse(JieArray.isEmpty(new short[]{1}));
        Assert.assertFalse(JieArray.isEmpty(new char[]{'a'}));
        Assert.assertFalse(JieArray.isEmpty(new int[]{1}));
        Assert.assertFalse(JieArray.isEmpty(new long[]{1L}));
        Assert.assertFalse(JieArray.isEmpty(new float[]{1.0f}));
        Assert.assertFalse(JieArray.isEmpty(new double[]{1.0}));

        // Test with null arrays
        Assert.assertFalse(JieArray.isNotEmpty((Object[]) null));
        Assert.assertFalse(JieArray.isNotEmpty((boolean[]) null));
        Assert.assertFalse(JieArray.isNotEmpty((byte[]) null));
        Assert.assertFalse(JieArray.isNotEmpty((short[]) null));
        Assert.assertFalse(JieArray.isNotEmpty((char[]) null));
        Assert.assertFalse(JieArray.isNotEmpty((int[]) null));
        Assert.assertFalse(JieArray.isNotEmpty((long[]) null));
        Assert.assertFalse(JieArray.isNotEmpty((float[]) null));
        Assert.assertFalse(JieArray.isNotEmpty((double[]) null));

        // Test with empty arrays
        Assert.assertFalse(JieArray.isNotEmpty(new Object[0]));
        Assert.assertFalse(JieArray.isNotEmpty(new boolean[0]));
        Assert.assertFalse(JieArray.isNotEmpty(new byte[0]));
        Assert.assertFalse(JieArray.isNotEmpty(new short[0]));
        Assert.assertFalse(JieArray.isNotEmpty(new char[0]));
        Assert.assertFalse(JieArray.isNotEmpty(new int[0]));
        Assert.assertFalse(JieArray.isNotEmpty(new long[0]));
        Assert.assertFalse(JieArray.isNotEmpty(new float[0]));
        Assert.assertFalse(JieArray.isNotEmpty(new double[0]));

        // Test with non-empty arrays
        Assert.assertTrue(JieArray.isNotEmpty(new Object[]{1}));
        Assert.assertTrue(JieArray.isNotEmpty(new boolean[]{true}));
        Assert.assertTrue(JieArray.isNotEmpty(new byte[]{1}));
        Assert.assertTrue(JieArray.isNotEmpty(new short[]{1}));
        Assert.assertTrue(JieArray.isNotEmpty(new char[]{'a'}));
        Assert.assertTrue(JieArray.isNotEmpty(new int[]{1}));
        Assert.assertTrue(JieArray.isNotEmpty(new long[]{1L}));
        Assert.assertTrue(JieArray.isNotEmpty(new float[]{1.0f}));
        Assert.assertTrue(JieArray.isNotEmpty(new double[]{1.0}));
    }

    @Test
    public void testGet() {
        Integer[] objArray = {1, 2, 3, 4, 5};
        Assert.assertEquals(JieArray.get(objArray, 2, 0), 3);
        Assert.assertEquals(JieArray.get(objArray, -1, 9), 9);
        Assert.assertEquals(JieArray.get(objArray, 5, 9), 9);
        Assert.assertEquals(JieArray.get(null, 0, (Integer) 9), 9);
        Assert.assertEquals(JieArray.get(new Integer[]{null}, 0, 9), 9);

        int[] intArray = {1, 2, 3, 4, 5};
        Assert.assertEquals(JieArray.get(intArray, 2, 0), 3);
        Assert.assertEquals(JieArray.get(intArray, -1, 0), 0);
        Assert.assertEquals(JieArray.get(intArray, 5, 0), 0);
        Assert.assertEquals(JieArray.get((int[]) null, 0, 9), 9);

        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        Assert.assertEquals(JieArray.get(longArray, 1, 0L), 20L);
        Assert.assertEquals(JieArray.get(longArray, -1, 0L), 0L);
        Assert.assertEquals(JieArray.get(longArray, 5, 0L), 0L);
        Assert.assertEquals(JieArray.get((long[]) null, 0, 9), 9);

        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        Assert.assertEquals(JieArray.get(floatArray, 3, 0.0f), 4.5f);
        Assert.assertEquals(JieArray.get(floatArray, -1, 0.0f), 0.0f);
        Assert.assertEquals(JieArray.get(floatArray, 5, 0.0f), 0.0f);
        Assert.assertEquals(JieArray.get((float[]) null, 0, 9), 9);

        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        Assert.assertEquals(JieArray.get(doubleArray, 2, 0.0), 3.5);
        Assert.assertEquals(JieArray.get(doubleArray, -1, 0.0), 0.0);
        Assert.assertEquals(JieArray.get(doubleArray, 5, 0.0), 0.0);
        Assert.assertEquals(JieArray.get((double[]) null, 0, 9), 9);

        boolean[] booleanArray = {true, false, true, false, true};
        Assert.assertEquals(JieArray.get(booleanArray, 1, false), false);
        Assert.assertEquals(JieArray.get(booleanArray, -1, false), false);
        Assert.assertEquals(JieArray.get(booleanArray, 5, false), false);
        Assert.assertEquals(JieArray.get((boolean[]) null, 0, false), false);

        byte[] byteArray = {1, 2, 3, 4, 5};
        Assert.assertEquals(JieArray.get(byteArray, 4, (byte) 0), (byte) 5);
        Assert.assertEquals(JieArray.get(byteArray, -1, (byte) 0), (byte) 0);
        Assert.assertEquals(JieArray.get(byteArray, 5, (byte) 0), (byte) 0);
        Assert.assertEquals(JieArray.get((byte[]) null, 0, (byte) 9), 9);

        short[] shortArray = {100, 200, 300, 400, 500};
        Assert.assertEquals(JieArray.get(shortArray, 0, (short) 0), (short) 100);
        Assert.assertEquals(JieArray.get(shortArray, -1, (short) 0), (short) 0);
        Assert.assertEquals(JieArray.get(shortArray, 5, (short) 0), (short) 0);
        Assert.assertEquals(JieArray.get((short[]) null, 0, (short) 9), 9);

        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        Assert.assertEquals(JieArray.get(charArray, 3, 'x'), 'd');
        Assert.assertEquals(JieArray.get(charArray, -1, 'x'), 'x');
        Assert.assertEquals(JieArray.get(charArray, 5, 'x'), 'x');
        Assert.assertEquals(JieArray.get((char[]) null, 0, '9'), '9');
    }

    @Test
    public void testIndexOf() {
        // Test object array
        Integer[] objArray = {1, 2, 3, 4, 5, 3, 2, 1};
        Assert.assertEquals(JieArray.indexOf(objArray, 3), 2);
        Assert.assertEquals(JieArray.indexOf(objArray, 6), -1);
        Assert.assertEquals(JieArray.indexOf(objArray, 1), 0);
        Assert.assertEquals(JieArray.lastIndexOf(objArray, 3), 5);
        Assert.assertEquals(JieArray.lastIndexOf(objArray, 6), -1);
        Assert.assertEquals(JieArray.lastIndexOf(objArray, 1), 7);

        // Test int array
        int[] intArray = {1, 2, 3, 4, 5, 3, 2, 1};
        Assert.assertEquals(JieArray.indexOf(intArray, 3), 2);
        Assert.assertEquals(JieArray.indexOf(intArray, 6), -1);
        Assert.assertEquals(JieArray.indexOf(intArray, 1), 0);
        Assert.assertEquals(JieArray.lastIndexOf(intArray, 3), 5);
        Assert.assertEquals(JieArray.lastIndexOf(intArray, 6), -1);
        Assert.assertEquals(JieArray.lastIndexOf(intArray, 1), 7);

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L, 30L, 20L, 10L};
        Assert.assertEquals(JieArray.indexOf(longArray, 30L), 2);
        Assert.assertEquals(JieArray.indexOf(longArray, 60L), -1);
        Assert.assertEquals(JieArray.indexOf(longArray, 10L), 0);
        Assert.assertEquals(JieArray.lastIndexOf(longArray, 30L), 5);
        Assert.assertEquals(JieArray.lastIndexOf(longArray, 60L), -1);
        Assert.assertEquals(JieArray.lastIndexOf(longArray, 10L), 7);

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f, 3.5f, 2.5f, 1.5f};
        Assert.assertEquals(JieArray.indexOf(floatArray, 3.5f), 2);
        Assert.assertEquals(JieArray.indexOf(floatArray, 6.5f), -1);
        Assert.assertEquals(JieArray.indexOf(floatArray, 1.5f), 0);
        Assert.assertEquals(JieArray.lastIndexOf(floatArray, 3.5f), 5);
        Assert.assertEquals(JieArray.lastIndexOf(floatArray, 6.5f), -1);
        Assert.assertEquals(JieArray.lastIndexOf(floatArray, 1.5f), 7);

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5, 3.5, 2.5, 1.5};
        Assert.assertEquals(JieArray.indexOf(doubleArray, 3.5), 2);
        Assert.assertEquals(JieArray.indexOf(doubleArray, 6.5), -1);
        Assert.assertEquals(JieArray.indexOf(doubleArray, 1.5), 0);
        Assert.assertEquals(JieArray.lastIndexOf(doubleArray, 3.5), 5);
        Assert.assertEquals(JieArray.lastIndexOf(doubleArray, 6.5), -1);
        Assert.assertEquals(JieArray.lastIndexOf(doubleArray, 1.5), 7);

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true, true, false, true};
        Assert.assertEquals(JieArray.indexOf(booleanArray, true), 0);
        Assert.assertEquals(JieArray.indexOf(booleanArray, false), 1);
        Assert.assertEquals(JieArray.lastIndexOf(booleanArray, true), 7);
        Assert.assertEquals(JieArray.lastIndexOf(booleanArray, false), 6);
        Assert.assertEquals(JieArray.indexOf(new boolean[]{false}, true), -1);
        Assert.assertEquals(JieArray.lastIndexOf(new boolean[]{false}, true), -1);

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5, 3, 2, 1};
        Assert.assertEquals(JieArray.indexOf(byteArray, (byte) 3), 2);
        Assert.assertEquals(JieArray.indexOf(byteArray, (byte) 6), -1);
        Assert.assertEquals(JieArray.indexOf(byteArray, (byte) 1), 0);
        Assert.assertEquals(JieArray.lastIndexOf(byteArray, (byte) 3), 5);
        Assert.assertEquals(JieArray.lastIndexOf(byteArray, (byte) 6), -1);
        Assert.assertEquals(JieArray.lastIndexOf(byteArray, (byte) 1), 7);

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500, 300, 200, 100};
        Assert.assertEquals(JieArray.indexOf(shortArray, (short) 300), 2);
        Assert.assertEquals(JieArray.indexOf(shortArray, (short) 600), -1);
        Assert.assertEquals(JieArray.indexOf(shortArray, (short) 100), 0);
        Assert.assertEquals(JieArray.lastIndexOf(shortArray, (short) 300), 5);
        Assert.assertEquals(JieArray.lastIndexOf(shortArray, (short) 600), -1);
        Assert.assertEquals(JieArray.lastIndexOf(shortArray, (short) 100), 7);

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e', 'c', 'b', 'a'};
        Assert.assertEquals(JieArray.indexOf(charArray, 'c'), 2);
        Assert.assertEquals(JieArray.indexOf(charArray, 'f'), -1);
        Assert.assertEquals(JieArray.indexOf(charArray, 'a'), 0);
        Assert.assertEquals(JieArray.lastIndexOf(charArray, 'c'), 5);
        Assert.assertEquals(JieArray.lastIndexOf(charArray, 'f'), -1);
        Assert.assertEquals(JieArray.lastIndexOf(charArray, 'a'), 7);
    }

    @Test
    public void testAsList() {
        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = JieArray.asList(intArray);
        Assert.assertEquals(intList.size(), 5);
        Assert.assertEquals(intList.get(2), 3);
        Integer oldValueInt = intList.set(2, 10); // 修改索引 2 的元素为 10
        Assert.assertEquals(oldValueInt, 3);
        Assert.assertEquals(intList.get(2), 10);

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = JieArray.asList(longArray);
        Assert.assertEquals(longList.size(), 5);
        Assert.assertEquals(longList.get(2), 30L);
        Long oldValueLong = longList.set(2, 100L); // 修改索引 2 的元素为 100
        Assert.assertEquals(oldValueLong, 30L);
        Assert.assertEquals(longList.get(2), 100L);

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = JieArray.asList(floatArray);
        Assert.assertEquals(floatList.size(), 5);
        Assert.assertEquals(floatList.get(2), 3.5f);
        Float oldValueFloat = floatList.set(2, 10.5f); // 修改索引 2 的元素为 10.5
        Assert.assertEquals(oldValueFloat, 3.5f);
        Assert.assertEquals(floatList.get(2), 10.5f);

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = JieArray.asList(doubleArray);
        Assert.assertEquals(doubleList.size(), 5);
        Assert.assertEquals(doubleList.get(2), 3.5);
        Double oldValueDouble = doubleList.set(2, 10.5); // 修改索引 2 的元素为 10.5
        Assert.assertEquals(oldValueDouble, 3.5);
        Assert.assertEquals(doubleList.get(2), 10.5);

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = JieArray.asList(booleanArray);
        Assert.assertEquals(booleanList.size(), 5);
        Assert.assertEquals(booleanList.get(2), true);
        Boolean oldValueBoolean = booleanList.set(2, false); // 修改索引 2 的元素为 false
        Assert.assertEquals(oldValueBoolean, true);
        Assert.assertEquals(booleanList.get(2), false);

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = JieArray.asList(byteArray);
        Assert.assertEquals(byteList.size(), 5);
        Assert.assertEquals(byteList.get(2), (byte) 3);
        Byte oldValueByte = byteList.set(2, (byte) 10); // 修改索引 2 的元素为 10
        Assert.assertEquals(oldValueByte, (byte) 3);
        Assert.assertEquals(byteList.get(2), (byte) 10);

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = JieArray.asList(shortArray);
        Assert.assertEquals(shortList.size(), 5);
        Assert.assertEquals(shortList.get(2), (short) 300);
        Short oldValueShort = shortList.set(2, (short) 1000); // 修改索引 2 的元素为 1000
        Assert.assertEquals(oldValueShort, (short) 300);
        Assert.assertEquals(shortList.get(2), (short) 1000);

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = JieArray.asList(charArray);
        Assert.assertEquals(charList.size(), 5);
        Assert.assertEquals(charList.get(2), 'c');
        Character oldValueChar = charList.set(2, 'z'); // 修改索引 2 的元素为 'z'
        Assert.assertEquals(oldValueChar, 'c');
        Assert.assertEquals(charList.get(2), 'z');

        // Test string array
        String[] stringArray = {"hello", "world", "java", "test", "array"};
        List<String> stringList = JieArray.asList(stringArray);
        Assert.assertEquals(stringList.size(), 5);
        Assert.assertEquals(stringList.get(2), "java");
        String oldValueString = stringList.set(2, "modified"); // 修改索引 2 的元素为 "modified"
        Assert.assertEquals(oldValueString, "java");
        Assert.assertEquals(stringList.get(2), "modified");
    }

    @Test
    public void testListOf() {
        // Test int array
        int[] intArray = {1, 2, 3, 4, 5};
        List<Integer> intList = JieArray.listOf(intArray);
        Assert.assertEquals(intList.size(), 5);
        Assert.assertEquals(intList.get(2), 3);
        Assert.expectThrows(UnsupportedOperationException.class, () -> intList.set(2, 10));

        // Test long array
        long[] longArray = {10L, 20L, 30L, 40L, 50L};
        List<Long> longList = JieArray.listOf(longArray);
        Assert.assertEquals(longList.size(), 5);
        Assert.assertEquals(longList.get(2), 30L);
        Assert.expectThrows(UnsupportedOperationException.class, () -> longList.set(2, 100L));

        // Test float array
        float[] floatArray = {1.5f, 2.5f, 3.5f, 4.5f, 5.5f};
        List<Float> floatList = JieArray.listOf(floatArray);
        Assert.assertEquals(floatList.size(), 5);
        Assert.assertEquals(floatList.get(2), 3.5f);
        Assert.expectThrows(UnsupportedOperationException.class, () -> floatList.set(2, 10.5f));

        // Test double array
        double[] doubleArray = {1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> doubleList = JieArray.listOf(doubleArray);
        Assert.assertEquals(doubleList.size(), 5);
        Assert.assertEquals(doubleList.get(2), 3.5);
        Assert.expectThrows(UnsupportedOperationException.class, () -> doubleList.set(2, 10.5));

        // Test boolean array
        boolean[] booleanArray = {true, false, true, false, true};
        List<Boolean> booleanList = JieArray.listOf(booleanArray);
        Assert.assertEquals(booleanList.size(), 5);
        Assert.assertEquals(booleanList.get(2), true);
        Assert.expectThrows(UnsupportedOperationException.class, () -> booleanList.set(2, false));

        // Test byte array
        byte[] byteArray = {1, 2, 3, 4, 5};
        List<Byte> byteList = JieArray.listOf(byteArray);
        Assert.assertEquals(byteList.size(), 5);
        Assert.assertEquals(byteList.get(2), (byte) 3);
        Assert.expectThrows(UnsupportedOperationException.class, () -> byteList.set(2, (byte) 10));

        // Test short array
        short[] shortArray = {100, 200, 300, 400, 500};
        List<Short> shortList = JieArray.listOf(shortArray);
        Assert.assertEquals(shortList.size(), 5);
        Assert.assertEquals(shortList.get(2), (short) 300);
        Assert.expectThrows(UnsupportedOperationException.class, () -> shortList.set(2, (short) 1000));

        // Test char array
        char[] charArray = {'a', 'b', 'c', 'd', 'e'};
        List<Character> charList = JieArray.listOf(charArray);
        Assert.assertEquals(charList.size(), 5);
        Assert.assertEquals(charList.get(2), 'c');
        Assert.expectThrows(UnsupportedOperationException.class, () -> charList.set(2, 'z'));

        // Test string array
        String[] stringArray = {"hello", "world", "java", "test", "array"};
        List<String> stringList = JieArray.listOf(stringArray);
        Assert.assertEquals(stringList.size(), 5);
        Assert.assertEquals(stringList.get(2), "java");
        Assert.expectThrows(UnsupportedOperationException.class, () -> stringList.set(2, "modified"));
    }

    @Test
    public void testMap() {
        Character[] chars = {'a', 'b', 'c'};
        Integer[] asciiValues = {97, 98, 99};
        Assert.assertEquals(JieArray.map(chars, new Integer[0], c -> (int) c), asciiValues);
        Assert.assertEquals(JieArray.map(chars, new Integer[3], c -> (int) c), asciiValues);
        Assert.assertEquals(JieArray.map(chars, c -> (int) c), asciiValues);
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieArray.map(chars, c -> null));
        Integer[] asciiValues2 = {null, 98, 99};
        Assert.assertEquals(JieArray.map(chars, c -> c == 'a' ? null : (int) c), asciiValues2);
    }
}
