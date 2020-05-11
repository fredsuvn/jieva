package xyz.srclab.common.bytecode;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.collection.list.ListHelper;
import xyz.srclab.common.util.string.StringHelper;

import java.util.Collections;
import java.util.List;

/**
 * @author sunqian
 */
@Immutable
public class BNewType implements BDescribable {

    private final String name;
    private final @Immutable List<BTypeVariable> typeVariables;
    private final BType superClass;
    private final @Immutable List<BType> interfaces;

    private final String internalName;
    private final String descriptor;
    private @Nullable String signature;

    public BNewType(
            String className,
            @Nullable Iterable<BTypeVariable> typeVariables,
            @Nullable BType superClass,
            @Nullable Iterable<BType> interfaces
    ) {
        this.name = className;
        this.typeVariables = typeVariables == null ? Collections.emptyList() :
                ListHelper.immutable(IterableHelper.asList(typeVariables));
        this.superClass = superClass == null ? ByteCodeHelper.OBJECT : superClass;
        this.interfaces = interfaces == null ? Collections.emptyList() :
                ListHelper.immutable(IterableHelper.asList(interfaces));
        this.internalName = ByteCodeHelper.getTypeInternalName(className);
        this.descriptor = ByteCodeHelper.getTypeDescriptor(className);
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public List<BTypeVariable> getTypeVariables() {
        return typeVariables;
    }

    public BType getSuperClass() {
        return superClass;
    }

    public List<BType> getInterfaces() {
        return interfaces;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getSignature() {
        if (signature == null) {
            signature = getSignature0();
        }
        return signature;
    }

    private String getSignature0() {
        String typeVariablesDeclaration =
                "<" + StringHelper.join("", typeVariables, BTypeVariable::getDeclaration) + ">";
        String interfacesSignature = StringHelper.join("", interfaces, BDescribable::getSignature);
        return typeVariablesDeclaration + superClass.getSignature() + interfacesSignature;
    }
}
