package xyz.srclab.common.bytecode;

/**
 * @author sunqian
 */
public class BTypeHelper {

    public static final BType PRIMITIVE_VOID_TYPE = new BType(void.class);

    public static final BType OBJECT_TYPE = new BType(Object.class);

    public static final BArrayType OBJECT_ARRAY_TYPE = new BArrayType(OBJECT_TYPE, 1);

    public static final ByteCodeType[] EMPTY_TYPE_ARRAY = new ByteCodeType[0];

    public static final BTypeVariable[] EMPTY_TYPE_VARIABLE_ARRAY = new BTypeVariable[0];

    public static final ByteCodeType[] OBJECT_ARRAY_PARAMETER = {OBJECT_ARRAY_TYPE};
}
