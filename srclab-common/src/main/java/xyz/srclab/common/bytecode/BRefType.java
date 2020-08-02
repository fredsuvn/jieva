package xyz.srclab.common.bytecode;

import org.apache.commons.collections4.CollectionUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.IterableKit;
import xyz.srclab.common.string.StringKit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sunqian
 */
public class BRefType implements BType {

    private final String name;
    private final String internalName;
    private final String descriptor;

    private @Nullable List<BDescribable> genericTypes;

    public BRefType(Class<?> type) {
        this.name = type.getName();
        this.internalName = ByteCodeHelper.getTypeInternalName(type);
        this.descriptor = ByteCodeHelper.getTypeDescriptor(type);
    }

    public BRefType(String name) {
        this.name = name;
        this.internalName = ByteCodeHelper.getTypeInternalName(name);
        this.descriptor = ByteCodeHelper.getTypeDescriptor(name);
    }

    public void addGenericTypes(BDescribable... genericTypes) {
        addGenericTypes(Arrays.asList(genericTypes));
    }

    public void addGenericTypes(Iterable<BDescribable> genericTypes) {
        IterableKit.addAll(getGenericTypes(), genericTypes);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getSignature() {
        if (CollectionUtils.isEmpty(genericTypes)) {
            return descriptor;
        }
        return "L" +
                internalName +
                "<" +
                StringKit.join("", genericTypes, BDescribable::getSignature) +
                ">;";
    }

    private List<BDescribable> getGenericTypes() {
        if (genericTypes == null) {
            genericTypes = new LinkedList<>();
        }
        return genericTypes;
    }
}
