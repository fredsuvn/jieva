package test.java.xyz.srclab.common.reflect;

import xyz.srclab.common.test.TestLogger;

import java.util.Objects;

public class ReflectClass extends SuperReflectClass {

    private static final TestLogger logger = TestLogger.DEFAULT;

    static {
        logger.log("Load class: " + ReflectClass.class);
    }

    public final String publicField = "publicField";
    protected final String protectedField = "protectedField";
    private String privateField = "privateField";
    String packageField = "packageField";

    private final String param;

    public ReflectClass() {
        this("");
    }

    protected ReflectClass(String param) {
        this.param = param;
        logger.log("New instance: " + param);
    }

    private ReflectClass(String param0, String param1) {
        this(param0 + " : " + param1);
    }

    public ReflectClass(String param0, String param1, String param2) {
        this(param0 + " : " + param1 + " : " + param2);
    }

    public String publicMethod() {
        return publicField;
    }

    protected String protectedMethod() {
        return protectedField;
    }

    private String privateMethod() {
        return privateField;
    }

    String packageMethod() {
        return packageField;
    }

    @Override
    public int hashCode() {
        return param.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReflectClass && Objects.equals(param, ((ReflectClass) obj).param);
    }
}
