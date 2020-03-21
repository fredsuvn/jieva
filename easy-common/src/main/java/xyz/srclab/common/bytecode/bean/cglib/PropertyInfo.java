package xyz.srclab.common.bytecode.bean.cglib;

class PropertyInfo {

    private final String name;
    private final Class<?> type;

    PropertyInfo(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
}
