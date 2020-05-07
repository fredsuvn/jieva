package xyz.srclab.common.bytecode;

import org.apache.commons.lang3.StringUtils;

/**
 * @author sunqian
 */
public class BArrayType implements ByteCodeType {

    private final ByteCodeType componentType;
    private final String prefix;

    public BArrayType(ByteCodeType componentType, int dimension) {
        this.componentType = componentType;
        this.prefix = StringUtils.repeat('[', dimension);
    }

    @Override
    public String getDescriptor() {
        return prefix + componentType.getDescriptor();
    }

    @Override
    public String getSignature() {
        return prefix + componentType.getSignature();
    }
}
