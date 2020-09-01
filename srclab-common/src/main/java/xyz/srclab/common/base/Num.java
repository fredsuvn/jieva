package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public class Num {

    public static int toInt(@Nullable Number number) {
        return number == null ? 0 : number.intValue();
    }

    public static int toInt(@Nullable String number) {
        return number == null ? 0 : Integer.parseInt(number);
    }

    public static int toInt(@Nullable Object any) {
        if (any == null) {
            return 0;
        }
        return any instanceof Number ?
                ((Number) any).intValue() : Integer.parseInt(any.toString());
    }

    public static long toLong(@Nullable Number number) {
        return number == null ? 0 : number.longValue();
    }

    public static long toLong(@Nullable String number) {
        return number == null ? 0 : Long.parseLong(number);
    }

    public static long toLong(@Nullable Object any) {
        if (any == null) {
            return 0;
        }
        return any instanceof Number ?
                ((Number) any).longValue() : Long.parseLong(any.toString());
    }

    public static float toFloat(@Nullable Number number) {
        return number == null ? 0 : number.floatValue();
    }

    public static float toFloat(@Nullable String number) {
        return number == null ? 0 : Float.parseFloat(number);
    }

    public static float toFloat(@Nullable Object any) {
        if (any == null) {
            return 0;
        }
        return any instanceof Number ?
                ((Number) any).floatValue() : Float.parseFloat(any.toString());
    }

    public static double toDouble(@Nullable Number number) {
        return number == null ? 0 : number.doubleValue();
    }

    public static double toDouble(@Nullable String number) {
        return number == null ? 0 : Double.parseDouble(number);
    }

    public static double toDouble(@Nullable Object any) {
        if (any == null) {
            return 0;
        }
        return any instanceof Number ?
                ((Number) any).doubleValue() : Double.parseDouble(any.toString());
    }
}
