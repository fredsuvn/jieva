package xyz.srclab.common.array;

/**
 * @author sunqian
 */
final class PrimitiveArraySupport {

    private static final class OfInt implements ArrayRef {

        @Override
        public boolean[] toBooleanArray() {
            return new boolean[0];
        }

        @Override
        public byte[] toByteArray() {
            return new byte[0];
        }

        @Override
        public short[] toShortArray() {
            return new short[0];
        }

        @Override
        public char[] toCharArray() {
            return new char[0];
        }

        @Override
        public int[] toIntArray() {
            return new int[0];
        }

        @Override
        public long[] toLongArray() {
            return new long[0];
        }

        @Override
        public float[] toFloatArray() {
            return new float[0];
        }

        @Override
        public double[] toDoubleArray() {
            return new double[0];
        }
    }
}
