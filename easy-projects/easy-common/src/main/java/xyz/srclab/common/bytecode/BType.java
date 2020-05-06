package xyz.srclab.common.bytecode;

import com.google.common.collect.Iterables;
import xyz.srclab.common.util.string.StringHelper;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author sunqian
 */
public class BType implements ByteCodeType {

    public static final BType OBJECT_TYPE = new BType(Object.class);

    private final String name;
    private final String internalName;
    private final String descriptor;

    private final LinkedList<ByteCodeType> genericTypes = new LinkedList<>();

    public BType(Class<?> type) {
        this(type.getName());
    }

    public BType(String name) {
        this.name = name;
        this.internalName = ByteCodeHelper.getTypeInternalName(name);
        this.descriptor = ByteCodeHelper.getTypeDescriptor(name);
    }

    public void addGenericTypes(ByteCodeType... genericTypes) {
        addGenericTypes(Arrays.asList(genericTypes));
    }

    public void addGenericTypes(Iterable<ByteCodeType> genericTypes) {
        Iterables.addAll(this.genericTypes, genericTypes);
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getSignature() {
        if (genericTypes.isEmpty()) {
            return descriptor;
        }
        return descriptor +
                "<" +
                StringHelper.join("", genericTypes, ByteCodeType::getSignature) +
                ">;";
    }
}
