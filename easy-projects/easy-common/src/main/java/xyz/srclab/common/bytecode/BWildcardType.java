package xyz.srclab.common.bytecode;

/**
 * @author sunqian
 */
public interface BWildcardType extends BType {

    static BWildcardType newBWildcardType(BType[] bounds, boolean upper) {
        return BTypeSupport.newBWildcardType(bounds, upper);
    }

    BType[] getUpperBounds();

    BType[] getLowerBounds();
}
