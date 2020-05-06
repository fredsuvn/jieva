package xyz.srclab.common.bytecode;

/**
 * @author sunqian
 */
public interface BTypeVariable extends BType {

    static BTypeVariable newBTypeVariable(String name, BType[] bounds, boolean isInterface) {
        return BTypeSupport.newBTypeVariable(name, bounds, isInterface);
    }

    String getName();

    String getDeclaration();
}
