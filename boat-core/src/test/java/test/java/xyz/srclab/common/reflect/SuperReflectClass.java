package test.java.xyz.srclab.common.reflect;

public class SuperReflectClass {

    public String superPublicField = "superPublicField";
    protected String superProtectedField = "superProtectedField";
    private final String superPrivateField = "superPrivateField";
    String superPackageField = "superPackageField";

    public String superPublicMethod() {
        return superPublicField;
    }

    protected String superProtectedMethod() {
        return superProtectedField;
    }

    private String superPrivateMethod() {
        return superPrivateField;
    }

    String superPackageMethod() {
        return superPackageField;
    }
}
