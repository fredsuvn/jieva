package xyz.srclab.helper.code;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.lang.reflect.Method;

public interface CodeGenerator {

    default void generate(String name, PrintStream printStream) {
        Method[] methods = getClass().getDeclaredMethods();
        String shouldBeName = "generate" + StringUtils.capitalize(name);
        for (Method method : methods) {
            if (method.getName().equals(shouldBeName)) {
                try {
                    method.setAccessible(true);
                    method.invoke(this, printStream);
                    return;
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        throw new IllegalArgumentException("Cannot find name: " + name);
    }
}
