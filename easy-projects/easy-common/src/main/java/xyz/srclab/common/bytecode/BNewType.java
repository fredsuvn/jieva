package xyz.srclab.common.bytecode;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.IterableKit;
import xyz.srclab.common.collection.ListHelper;
import xyz.srclab.common.string.StringHelper;

import java.util.Collections;
import java.util.List;

/**
 * @author sunqian
 */
@Immutable
public class BNewType implements BType {

    private final String name;
    private final @Immutable List<BTypeVariable> typeVariables;
    private final BRefType superClass;
    private final @Immutable List<BRefType> interfaces;

    private final String internalName;
    private final String descriptor;
    private @Nullable String signature;

    public BNewType(
            String className,
            @Nullable Iterable<BTypeVariable> typeVariables,
            @Nullable BRefType superClass,
            @Nullable Iterable<BRefType> interfaces
    ) {
        this.name = className;
        this.typeVariables = typeVariables == null ? Collections.emptyList() :
                ListHelper.immutable(IterableKit.asList(typeVariables));
        this.superClass = superClass == null ? ByteCodeHelper.OBJECT : superClass;
        this.interfaces = interfaces == null ? Collections.emptyList() :
                ListHelper.immutable(IterableKit.asList(interfaces));
        this.internalName = ByteCodeHelper.getTypeInternalName(className);
        this.descriptor = ByteCodeHelper.getTypeDescriptor(className);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    public List<BTypeVariable> getTypeVariables() {
        return typeVariables;
    }

    public BRefType getSuperClass() {
        return superClass;
    }

    public List<BRefType> getInterfaces() {
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
