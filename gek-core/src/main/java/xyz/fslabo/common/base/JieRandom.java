// package xyz.fslabo.common.base;
//
// import java.util.concurrent.ThreadLocalRandom;
//
// /**
//  * Random utilities.
//  *
//  * @author fredsuvn
//  */
// public class JieRandom {
//
//     /**
//      * Returns next random value.
//      *
//      * @return next random value
//      */
//     public static boolean nextBoolean() {
//         return random().nextBoolean();
//     }
//
//     /**
//      * Fills given array with random value and returns.
//      *
//      * @param array given array
//      * @return given array
//      */
//     public static boolean[] fill(boolean[] array) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextBoolean();
//         }
//         return array;
//     }
//
//     /**
//      * Returns next random value.
//      *
//      * @return next random value
//      */
//     public static byte nextByte() {
//         return random().nextInt();
//     }
//
//     /**
//      * Returns next random value between specified start value inclusive and end value exclusive.
//      *
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return next random value between specified start value inclusive and end value exclusive
//      */
//     public static byte nextByte(int startInclusive, int endExclusive) {
//         return random().nextInt(startInclusive, endExclusive);
//     }
//
//     /**
//      * Fills given array with random value and returns.
//      *
//      * @param array given array
//      * @return given array
//      */
//     public static byte[] fill(byte[] array) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextByte();
//         }
//         return array;
//     }
//
//     /**
//      * Fills given array with random value between specified start value inclusive and end value exclusive, and
//      * returns.
//      *
//      * @param array          given array
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return given array
//      */
//     public static byte[] fill(byte[] array, int startInclusive, int endExclusive) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt(startInclusive, endExclusive);
//         }
//         return array;
//     }
//
//
//     /**
//      * Returns next random value.
//      *
//      * @return next random value
//      */
//     public static int nextInt() {
//         return random().nextInt();
//     }
//
//     /**
//      * Returns next random value between specified start value inclusive and end value exclusive.
//      *
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return next random value between specified start value inclusive and end value exclusive
//      */
//     public static int nextInt(int startInclusive, int endExclusive) {
//         return random().nextInt(startInclusive, endExclusive);
//     }
//
//     /**
//      * Fills given array with random value and returns.
//      *
//      * @param array given array
//      * @return given array
//      */
//     public static int[] fill(int[] array) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt();
//         }
//         return array;
//     }
//
//     /**
//      * Fills given array with random value between specified start value inclusive and end value exclusive, and
//      * returns.
//      *
//      * @param array          given array
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return given array
//      */
//     public static int[] fill(int[] array, int startInclusive, int endExclusive) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt(startInclusive, endExclusive);
//         }
//         return array;
//     }
//
//
//     /**
//      * Returns next random value.
//      *
//      * @return next random value
//      */
//     public static int nextInt() {
//         return random().nextInt();
//     }
//
//     /**
//      * Returns next random value between specified start value inclusive and end value exclusive.
//      *
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return next random value between specified start value inclusive and end value exclusive
//      */
//     public static int nextInt(int startInclusive, int endExclusive) {
//         return random().nextInt(startInclusive, endExclusive);
//     }
//
//     /**
//      * Fills given array with random value and returns.
//      *
//      * @param array given array
//      * @return given array
//      */
//     public static int[] fill(int[] array) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt();
//         }
//         return array;
//     }
//
//     /**
//      * Fills given array with random value between specified start value inclusive and end value exclusive, and
//      * returns.
//      *
//      * @param array          given array
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return given array
//      */
//     public static int[] fill(int[] array, int startInclusive, int endExclusive) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt(startInclusive, endExclusive);
//         }
//         return array;
//     }
//
//     /**
//      * Returns next random value.
//      *
//      * @return next random value
//      */
//     public static int nextInt() {
//         return random().nextInt();
//     }
//
//     /**
//      * Returns next random value between specified start value inclusive and end value exclusive.
//      *
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return next random value between specified start value inclusive and end value exclusive
//      */
//     public static int nextInt(int startInclusive, int endExclusive) {
//         return random().nextInt(startInclusive, endExclusive);
//     }
//
//     /**
//      * Fills given array with random value and returns.
//      *
//      * @param array given array
//      * @return given array
//      */
//     public static int[] fill(int[] array) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt();
//         }
//         return array;
//     }
//
//     /**
//      * Fills given array with random value between specified start value inclusive and end value exclusive, and
//      * returns.
//      *
//      * @param array          given array
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return given array
//      */
//     public static int[] fill(int[] array, int startInclusive, int endExclusive) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt(startInclusive, endExclusive);
//         }
//         return array;
//     }
//
//     /**
//      * Returns next random value.
//      *
//      * @return next random value
//      */
//     public static int nextInt() {
//         return random().nextInt();
//     }
//
//     /**
//      * Returns next random value between specified start value inclusive and end value exclusive.
//      *
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return next random value between specified start value inclusive and end value exclusive
//      */
//     public static int nextInt(int startInclusive, int endExclusive) {
//         return random().nextInt(startInclusive, endExclusive);
//     }
//
//     /**
//      * Fills given array with random value and returns.
//      *
//      * @param array given array
//      * @return given array
//      */
//     public static int[] fill(int[] array) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt();
//         }
//         return array;
//     }
//
//     /**
//      * Fills given array with random value between specified start value inclusive and end value exclusive, and
//      * returns.
//      *
//      * @param array          given array
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return given array
//      */
//     public static int[] fill(int[] array, int startInclusive, int endExclusive) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt(startInclusive, endExclusive);
//         }
//         return array;
//     }
//
//     /**
//      * Returns next random value.
//      *
//      * @return next random value
//      */
//     public static int nextInt() {
//         return random().nextInt();
//     }
//
//     /**
//      * Returns next random value between specified start value inclusive and end value exclusive.
//      *
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return next random value between specified start value inclusive and end value exclusive
//      */
//     public static int nextInt(int startInclusive, int endExclusive) {
//         return random().nextInt(startInclusive, endExclusive);
//     }
//
//     /**
//      * Fills given array with random value and returns.
//      *
//      * @param array given array
//      * @return given array
//      */
//     public static int[] fill(int[] array) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt();
//         }
//         return array;
//     }
//
//     /**
//      * Fills given array with random value between specified start value inclusive and end value exclusive, and
//      * returns.
//      *
//      * @param array          given array
//      * @param startInclusive specified start value
//      * @param endExclusive   specified end value
//      * @return given array
//      */
//     public static int[] fill(int[] array, int startInclusive, int endExclusive) {
//         for (int i = 0; i < array.length; i++) {
//             array[i] = nextInt(startInclusive, endExclusive);
//         }
//         return array;
//     }
//
//     private static ThreadLocalRandom random() {
//         return ThreadLocalRandom.current();
//     }
// }
