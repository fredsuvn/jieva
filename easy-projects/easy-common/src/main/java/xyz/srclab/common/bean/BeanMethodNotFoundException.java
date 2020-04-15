package xyz.srclab.common.bean;

public class BeanMethodNotFoundException extends RuntimeException {

    private final String methodSignature;

    public BeanMethodNotFoundException(String methodSignature) {
        super("Method: " + methodSignature);
        this.methodSignature = methodSignature;
    }

    public String getMethodSignature() {
        return methodSignature;
    }
}
