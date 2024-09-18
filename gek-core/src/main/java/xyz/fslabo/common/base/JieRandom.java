package xyz.fslabo.common.base;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Random utilities.
 *
 * @author fredsuvn
 */
public class JieRandom {

    /**
     * Returns next random boolean value.
     *
     * @return next random boolean value
     */
    public static boolean nextBoolean() {
        return random().nextBoolean();
    }

    /**
     * Fills given array with random boolean value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static boolean[] fill(boolean[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextBoolean();
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static byte nextByte() {
        return (byte) nextInt();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static byte nextByte(byte startInclusive, byte endExclusive) {
        return nextByte((int) startInclusive, (int) endExclusive);
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static byte nextByte(int startInclusive, int endExclusive) {
        return (byte) nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static byte[] fill(byte[] array) {
        random().nextBytes(array);
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static byte[] fill(byte[] array, byte startInclusive, byte endExclusive) {
        return fill(array, (int) startInclusive, (int) endExclusive);
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static byte[] fill(byte[] array, int startInclusive, int endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextByte(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static short nextShort() {
        return (short) nextInt();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static short nextShort(short startInclusive, short endExclusive) {
        return nextShort((int) startInclusive, (int) endExclusive);
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static short nextShort(int startInclusive, int endExclusive) {
        return (short) nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static short[] fill(short[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextShort();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static short[] fill(short[] array, short startInclusive, short endExclusive) {
        return fill(array, (int) startInclusive, (int) endExclusive);
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static short[] fill(short[] array, int startInclusive, int endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextShort(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static char nextChar() {
        return (char) nextInt();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static char nextChar(char startInclusive, char endExclusive) {
        return nextChar((int) startInclusive, (int) endExclusive);
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static char nextChar(int startInclusive, int endExclusive) {
        return (char) nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static char[] fill(char[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextChar();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static char[] fill(char[] array, char startInclusive, char endExclusive) {
        return fill(array, (int) startInclusive, (int) endExclusive);
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static char[] fill(char[] array, int startInclusive, int endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextChar(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static int nextInt() {
        return random().nextInt();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static int nextInt(int startInclusive, int endExclusive) {
        return random().nextInt(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static int[] fill(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextInt();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static int[] fill(int[] array, int startInclusive, int endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextInt(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static long nextLong() {
        return random().nextLong();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static long nextLong(long startInclusive, long endExclusive) {
        return random().nextLong(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static long[] fill(long[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextLong();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static long[] fill(long[] array, long startInclusive, long endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextLong(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static float nextFloat() {
        return random().nextFloat();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static float nextFloat(float startInclusive, float endExclusive) {
        return (float) nextDouble(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static float[] fill(float[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextFloat();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static float[] fill(float[] array, float startInclusive, float endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextFloat(startInclusive, endExclusive);
        }
        return array;
    }

    /**
     * Returns next random value.
     *
     * @return next random value
     */
    public static double nextDouble() {
        return random().nextDouble();
    }

    /**
     * Returns next random value between specified start value inclusive and end value exclusive.
     *
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return next random value between specified start value inclusive and end value exclusive
     */
    public static double nextDouble(double startInclusive, double endExclusive) {
        return random().nextDouble(startInclusive, endExclusive);
    }

    /**
     * Fills given array with random value and returns.
     *
     * @param array given array
     * @return given array
     */
    public static double[] fill(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextDouble();
        }
        return array;
    }

    /**
     * Fills given array with random value between specified start value inclusive and end value exclusive, and
     * returns.
     *
     * @param array          given array
     * @param startInclusive specified start value inclusive
     * @param endExclusive   specified end value exclusive
     * @return given array
     */
    public static double[] fill(double[] array, double startInclusive, double endExclusive) {
        for (int i = 0; i < array.length; i++) {
            array[i] = nextDouble(startInclusive, endExclusive);
        }
        return array;
    }

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }
}
