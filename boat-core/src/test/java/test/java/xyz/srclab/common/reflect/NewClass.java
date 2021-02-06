package test.java.xyz.srclab.common.reflect;

import xyz.srclab.common.test.TestLogger;

import java.util.Objects;

public class NewClass extends SuperNewClass {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    static {
        testLogger.log("Load class: " + NewClass.class);
    }

    public final String publicField = "publicField";
    protected final String protectedField = "protectedField";
    private String privateField = "privateField";
    String packageField = "packageField";

    private final String param;

    public NewClass() {
        this("");
    }

    protected NewClass(String param) {
        this.param = param;
        testLogger.log("New instance: " + param);
    }

    private NewClass(String param0, String param1) {
        this(param0 + " : " + param1);
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
        return obj instanceof NewClass && Objects.equals(param, ((NewClass) obj).param);
    }
}
