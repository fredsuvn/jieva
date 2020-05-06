package xyz.srclab.common.bytecode;

/**
 * @author sunqian
 */
public class BWildcardType implements ByteCodeType {

    public static final String NO_BOUND = "";

    public static final String UPPER_BOUND = "+";

    public static final String LOWER_BOUND = "-";

    private final String boundDescriptor;
    private final ByteCodeType bound;

    public BWildcardType(String boundDescriptor, ByteCodeType bound) {
        this.boundDescriptor = boundDescriptor;
        this.bound = bound;
    }

    @Override
    public String getDescriptor() {
        return LOWER_BOUND.equals(boundDescriptor) ? BType.OBJECT_TYPE.getDescriptor() : bound.getDescriptor();
    }

    @Override
    public String getSignature() {
        return boundDescriptor + bound.getSignature();
    }
}
