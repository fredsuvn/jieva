package xyz.srclab.common.bytecode;

public interface BType {

    static BType of(Class<?> cls) {
        return BTypeSupport.newBType(cls);
    }

    static BType of(String className, BType[] genericTypes, boolean array) {
        return BTypeSupport.newBType(className, genericTypes, array);
    }

    String getClassName();

    String getInternalName();

    String getDescriptor();

    String getSignature();
}
