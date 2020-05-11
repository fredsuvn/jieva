package xyz.srclab.common.bytecode;

import org.apache.commons.collections4.CollectionUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.util.string.StringHelper;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author sunqian
 */
public class BTypeVariable implements BDescribable {

    private final String name;
    private final boolean isInterface;
    private @Nullable LinkedList<BDescribable> bounds;

    public BTypeVariable(String name, boolean isInterface) {
        this.name = name;
        this.isInterface = isInterface;
    }

    public void addBounds(BDescribable... bounds) {
        addBounds(Arrays.asList(bounds));
    }

    public void addBounds(Iterable<BDescribable> bounds) {
        IterableHelper.addAll(getBounds(), bounds);
    }

    public String getDeclaration() {
        if (CollectionUtils.isEmpty(bounds)) {
            return name + ":" + ByteCodeHelper.OBJECT.getDescriptor();
        }
        return name +
                (isInterface ? "::" : ":") +
                StringHelper.join(":", bounds, BDescribable::getSignature);
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

    private BDescribable getNearly() {
        return CollectionUtils.isEmpty(bounds) ? ByteCodeHelper.OBJECT : bounds.getFirst();
    }

    private LinkedList<BDescribable> getBounds() {
        if (bounds == null) {
            bounds = new LinkedList<>();
        }
        return bounds;
    }
}
