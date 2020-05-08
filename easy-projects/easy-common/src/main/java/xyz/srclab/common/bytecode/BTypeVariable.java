package xyz.srclab.common.bytecode;

import com.google.common.collect.Iterables;
import xyz.srclab.common.util.string.StringHelper;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author sunqian
 */
public class BTypeVariable implements ByteCodeType {

    private final String name;
    private final boolean isInterface;
    private final LinkedList<ByteCodeType> bounds = new LinkedList<>();

    public BTypeVariable(String name, boolean isInterface) {
        this.name = name;
        this.isInterface = isInterface;
    }

    public void addBounds(ByteCodeType... bounds) {
        addBounds(Arrays.asList(bounds));
    }

    public void addBounds(Iterable<ByteCodeType> bounds) {
        Iterables.addAll(this.bounds, bounds);
    }

    public String getDeclaration() {
        if (bounds.isEmpty()) {
            return name + ":" + BTypeHelper.OBJECT_TYPE.getDescriptor();
        }
        return name +
                (isInterface ? "::" : ":") +
                StringHelper.join(":", bounds, ByteCodeType::getSignature);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getDescriptor() {
        return getNearly().getDescriptor();
    }

    @Override
    public String getSignature() {
        return "T" + name + ";";
    }

    private ByteCodeType getNearly() {
        return bounds.isEmpty() ? BTypeHelper.OBJECT_TYPE : bounds.getFirst();
    }
}
