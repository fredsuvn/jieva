package xyz.srclab.sample.array;

import xyz.srclab.common.array.ArrayHelper;

import java.util.Arrays;

/**
 * @author sunqian
 */
public class ArraySample {

    public static void main(String[] args) {
        int[] array = ArrayHelper.newArray(new int[100], i -> i);
        System.out.println(Arrays.toString(array));
    }
}
