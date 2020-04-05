package xyz.srclab.common.reflect;

import java.lang.reflect.Method;

public class SignatureHelper {

    public static String signMethod(Method method) {
        return signMethod(method.getName(), method.getParameterTypes());
    }

    public static String signMethod(String methodName, Class<?>[] parameterTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append(methodName);
        buf.append("(");
        switch (parameterTypes.length) {
            case 0:
                buf.append(")");
                return buf.toString();
            case 1:
                buf.append(parameterTypes[0].getName());
                buf.append(")");
                return buf.toString();
            default:
                for (Class<?> parameterType : parameterTypes) {
                    buf.append(parameterType.getName());
                    buf.append(",");
                }
                buf.replace(buf.length() - 1, buf.length(), ")");
                return buf.toString();
        }
    }
}
