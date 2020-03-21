package xyz.srclab.common.reflect;

public interface PropertyDefinition {

    static PropertyDefinition of(String name, Class<?> type, boolean readable, boolean writeable) {
        return new PropertyDefinition() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public boolean isReadable() {
                return readable;
            }

            @Override
            public boolean isWriteable() {
                return writeable;
            }
        };
    }

    String getName();

    Class<?> getType();

    boolean isReadable();

    boolean isWriteable();
}
