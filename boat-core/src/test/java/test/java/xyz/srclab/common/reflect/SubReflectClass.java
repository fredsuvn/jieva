package test.java.xyz.srclab.common.reflect;

public class SubReflectClass extends ReflectClass {

    public String subPublicField = "subPublicField";
    protected String subProtectedField = "subProtectedField";
    private final String subPrivateField = "subPrivateField";
    String subPackageField = "subPackageField";


    public String subPublicMethod() {
        return subPublicField;
    }

    protected String subProtectedMethod() {
        return subProtectedField;
    }

    private String subPrivateMethod() {
        return subPrivateField;
    }

    String subPackageMethod() {
        return subPackageField;
    }
}
