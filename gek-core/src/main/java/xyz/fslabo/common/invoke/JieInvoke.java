package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.invoke.MethodHandle;

/**
 * Utilities for invoking operation.
 *
 * @author fredsuvn
 */
public class JieInvoke {

    /**
     * Invokes {@link MethodHandle} of virtual with given instance and arguments.
     *
     * @param methodHandle method handle to be invoked
     * @param inst         given instance
     * @param args         given arguments
     * @return result of invocation
     * @throws Throwable anything thrown by the target method invocation
     */
    public static @Nullable Object invokeVirtual(MethodHandle methodHandle, @Nullable Object inst, Object... args) throws Throwable {
        switch (args.length) {
            case 0:
                return methodHandle.invoke(inst);
            case 1:
                return methodHandle.invoke(inst, args[0]);
            case 2:
                return methodHandle.invoke(inst, args[0], args[1]);
            case 3:
                return methodHandle.invoke(inst, args[0], args[1], args[2]);
            case 4:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3]);
            case 5:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                Object[] actualArgs = new Object[args.length + 1];
                actualArgs[0] = inst;
                System.arraycopy(args, 0, actualArgs, 1, args.length);
                return methodHandle.invokeWithArguments(actualArgs);
        }
    }

    /**
     * Invokes {@link MethodHandle} of static with given arguments.
     *
     * @param methodHandle method handle to be invoked
     * @param args         given arguments
     * @return result of invocation
     * @throws Throwable anything thrown by the target method invocation
     */
    public static @Nullable Object invokeStatic(MethodHandle methodHandle, Object... args) throws Throwable {
        switch (args.length) {
            case 0:
                return methodHandle.invoke();
            case 1:
                return methodHandle.invoke(args[0]);
            case 2:
                return methodHandle.invoke(args[0], args[1]);
            case 3:
                return methodHandle.invoke(args[0], args[1], args[2]);
            case 4:
                return methodHandle.invoke(args[0], args[1], args[2], args[3]);
            case 5:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                return methodHandle.invokeWithArguments(args);
        }
    }
}
