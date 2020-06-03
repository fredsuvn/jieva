package xyz.srclab.common.bean;

import xyz.srclab.annotation.Hide;

@Hide
public class BeanMethodNotFoundException extends RuntimeException {

    private final String methodDescription;

    public BeanMethodNotFoundException(String methodDescription) {
        super("Method: " + methodDescription);
        this.methodDescription = methodDescription;
    }

    public String getMethodDescription() {
        return methodDescription;
    }
}
