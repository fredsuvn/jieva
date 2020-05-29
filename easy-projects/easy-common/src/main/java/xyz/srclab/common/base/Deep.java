package xyz.srclab.common.base;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author sunqian
 */
@Deprecated
public class Deep {

    public static void deepWalk(Object object, Consumer<Object> consumer) {
        if (object instanceof Iterable) {
            deepWalkIterable((Iterable<?>) object, consumer);
            return;
        }
        if (object instanceof Map) {
            Set<? extends Map.Entry<?, ?>> set = ((Map<?, ?>) object).entrySet();
            deepWalkIterable(set, consumer);
            return;
        }
        if (object instanceof Object[]) {
            deepWalkArray((Object[]) object, consumer);
            return;
        }
        if (object instanceof byte[]) {
            deepWalkArray((byte[]) object, consumer);
            return;
        }
        if (object instanceof short[]) {
            deepWalkArray((short[]) object, consumer);
            return;
        }
        if (object instanceof char[]) {
            deepWalkArray((char[]) object, consumer);
            return;
        }
        if (object instanceof int[]) {
            deepWalkArray((int[]) object, consumer);
            return;
        }
        if (object instanceof long[]) {
            deepWalkArray((long[]) object, consumer);
            return;
        }
        if (object instanceof float[]) {
            deepWalkArray((float[]) object, consumer);
            return;
        }
        if (object instanceof double[]) {
            deepWalkArray((double[]) object, consumer);
            return;
        }
        if (object instanceof boolean[]) {
            deepWalkArray((boolean[]) object, consumer);
            return;
        }
        consumer.accept(object);
    }

    private static void deepWalkIterable(Iterable<?> iterable, Consumer<Object> consumer) {
        for (Object o : iterable) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(Object[] array, Consumer<Object> consumer) {
        for (Object o : array) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(byte[] array, Consumer<Object> consumer) {
        for (byte o : array) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(short[] array, Consumer<Object> consumer) {
        for (short o : array) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(char[] array, Consumer<Object> consumer) {
        for (char o : array) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(int[] array, Consumer<Object> consumer) {
        for (int o : array) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(long[] array, Consumer<Object> consumer) {
        for (long o : array) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(float[] array, Consumer<Object> consumer) {
        for (float o : array) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(double[] array, Consumer<Object> consumer) {
        for (double o : array) {
            consumer.accept(o);
        }
    }

    private static void deepWalkArray(boolean[] array, Consumer<Object> consumer) {
        for (boolean o : array) {
            consumer.accept(o);
        }
    }
}
