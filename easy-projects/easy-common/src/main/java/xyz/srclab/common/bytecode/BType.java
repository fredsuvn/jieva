package xyz.srclab.common.bytecode;

import org.apache.commons.collections4.CollectionUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.IterableHelper;
import xyz.srclab.common.string.StringHelper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sunqian
 */
public class BType implements BDescribable {

    private final String name;
    private final String internalName;
    private final String descriptor;

    private @Nullable List<BDescribable> genericTypes;

    public BType(Class<?> type) {
        this.name = type.getName();
        this.internalName = ByteCodeHelper.getTypeInternalName(type);
        this.descriptor = ByteCodeHelper.getTypeDescriptor(type);
    }

    public BType(String name) {
        this.name = name;
        this.internalName = ByteCodeHelper.getTypeInternalName(name);
        this.descriptor = ByteCodeHelper.getTypeDescriptor(name);
    }

    public void addGenericTypes(BDescribable... genericTypes) {
        addGenericTypes(Arrays.asList(genericTypes));
    }

    public void addGenericTypes(Iterable<BDescribable> genericTypes) {
        IterableHelper.addAll(getGenericTypes(), genericTypes);
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
        if (CollectionUtils.isEmpty(genericTypes)) {
            return descriptor;
        }
        return "L" +
                internalName +
                "<" +
                StringHelper.join("", genericTypes, BDescribable::getSignature) +
                ">;";
    }

    private List<BDescribable> getGenericTypes() {
        if (genericTypes == null) {
            genericTypes = new LinkedList<>();
        }
        return genericTypes;
    }
}
