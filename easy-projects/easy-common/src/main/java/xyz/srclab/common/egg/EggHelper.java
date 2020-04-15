package xyz.srclab.common.egg;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.reflect.method.MethodHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class EggHelper {

    public static Egg findEgg(String eggName) {
        @Nullable Class<?> eggClass = EnvironmentHelper.findClass(eggName);
        if (eggClass == null) {
            throw new IllegalArgumentException("Egg not found: " + eggName);
        }
        try {
            Method method = eggClass.getClass().getDeclaredMethod(
                    "getConstructor0", MethodHelper.EMPTY_PARAMETER_TYPES.getClass(), int.class);
            method.setAccessible(true);
            Constructor<?> eggConstructor = (Constructor<?>) method.invoke(
                    eggClass, MethodHelper.EMPTY_PARAMETER_TYPES, Member.DECLARED);
            eggConstructor.setAccessible(true);
            return (Egg) eggConstructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
