package xyz.srclab.common.bean;

public class BeanPropertyNotFoundException extends RuntimeException {

    private final String propertyName;

    public BeanPropertyNotFoundException(String propertyName) {
        super("Property: " + propertyName);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
