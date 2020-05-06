package xyz.srclab.common.bytecode;

public interface BNewType extends BType {

    static BNewType of(String className, BTypeVariable[] typeVariables, BType[] inheritances) {
        return BTypeSupport.newBNewBType(className, typeVariables, inheritances);
    }
}
