package xyz.srclab.common.bytecode;

import org.apache.commons.collections4.CollectionUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.IterableOps;
import xyz.srclab.common.reflect.TypeKit;
import xyz.srclab.common.string.StringKit;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author sunqian
 */
public class BTypeVariable implements BType {

    private final String name;
    private final boolean isInterface;
    private @Nullable LinkedList<BType> bounds;

    public BTypeVariable(String name, boolean isInterface) {
        this.name = name;
        this.isInterface = isInterface;
    }

    public BTypeVariable(TypeVariable<?> typeVariable) {
        this(typeVariable.getName(), TypeKit.getUpperBoundClass(typeVariable.getBounds()[0]).isInterface());
    }

    public void addBounds(BType... bounds) {
        addBounds(Arrays.asList(bounds));
    }

    public void addBounds(Iterable<BType> bounds) {
        IterableOps.addAll(getBounds(), bounds);
    }

    public String getDeclaration() {
        if (CollectionUtils.isEmpty(bounds)) {
            return name + ":" + ByteCodeHelper.OBJECT.getDescriptor();
        }
        return name +
                (isInterface ? "::" : ":") +
                StringKit.join(":", bounds, BDescribable::getSignature);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getInternalName() {
        return getNearly().getInternalName();
    }

    @Override
    public String getDescriptor() {
        return getNearly().getDescriptor();
    }

    @Override
    public String getSignature() {
        return "T" + name + ";";
    }

    private BType getNearly() {
        return CollectionUtils.isEmpty(bounds) ? ByteCodeHelper.OBJECT : bounds.getFirst();
    }

    private LinkedList<BType> getBounds() {
        if (bounds == null) {
            bounds = new LinkedList<>();
        }
        return bounds;
    }
}
