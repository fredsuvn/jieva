package xyz.srclab.jni;

import xyz.srclab.common.reflect.SignatureHelper;

import java.lang.reflect.Method;

public class JniSignature {

    public static String sign(Class<?> cls) {
        return SignatureHelper.signClass(cls);
    }

    public static String signMethod(Method method) {
        return signMethod(method.getParameterTypes(), method.getReturnType());
    }

    public static String signMethod(Class<?>[] parameterTypes, Class<?> returnType) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for (Class<?> parameterType : parameterTypes) {
            buf.append(sign(parameterType));
        }
        buf.append(")");
        buf.append(returnType);
        return buf.toString();
    }
}
