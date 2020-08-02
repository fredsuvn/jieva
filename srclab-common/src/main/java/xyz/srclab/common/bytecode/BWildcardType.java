package xyz.srclab.common.bytecode;

import xyz.srclab.annotation.Immutable;

/**
 * @author sunqian
 */
@Immutable
public class BWildcardType implements BDescribable {

    public static final String UPPER_BOUND = "+";

    public static final String LOWER_BOUND = "-";

    private final String boundDescriptor;
    private final BType bound;

    public BWildcardType(BType bound) {
        this("", bound);
    }

    public BWildcardType(String boundDescriptor, BType bound) {
        this.boundDescriptor = boundDescriptor;
        this.bound = bound;
    }

    @Override
    public String getDescriptor() {
        return LOWER_BOUND.equals(boundDescriptor) ? ByteCodeHelper.OBJECT.getDescriptor() : bound.getDescriptor();
    }

    @Override
    public String getSignature() {
        return boundDescriptor + bound.getSignature();
    }
}
