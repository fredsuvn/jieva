package xyz.srclab.common.bytecode;

import org.apache.commons.collections4.CollectionUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.IterableOps;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.string.StringKit;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author sunqian
 */
@Immutable
public class BMethod implements BDescribable {

    private final String name;
    private final BType returnType;
    private final @Immutable List<BType> parameterTypes;
    private final @Immutable List<BTypeVariable> typeVariables;

    private @Nullable String descriptor;
    private @Nullable String signature;

    public BMethod(
            String name,
            @Nullable BType returnType,
            @Nullable Iterable<BType> parameterTypes,
            @Nullable Iterable<BTypeVariable> typeVariables
    ) {
        this.name = name;
        this.returnType = returnType == null ? ByteCodeHelper.PRIMITIVE_VOID : returnType;
        this.parameterTypes = parameterTypes == null ? Collections.emptyList() :
                ListOps.immutable(IterableOps.asList(parameterTypes));
        this.typeVariables = typeVariables == null ? Collections.emptyList() :
                ListOps.immutable(IterableOps.asList(typeVariables));
    }

    public BMethod(Method method) {
        this(
                method.getName(),
                new BRefType(method.getReturnType()),
                ListOps.map(method.getParameterTypes(), BRefType::new),
                ListOps.map(method.getTypeParameters(), BTypeVariable::new)
        );
    }

    public String getName() {
        return name;
    }

    public BType getReturnType() {
        return returnType;
    }

    public BType getParameterType(int index) {
        return parameterTypes.get(index);
    }

    public List<BType> getParameterTypes() {
        return parameterTypes;
    }

    public BTypeVariable getBTypeVariable(int index) {
        return typeVariables.get(index);
    }

    public List<BTypeVariable> getTypeVariables() {
        return typeVariables;
    }

    @Override
    public String getDescriptor() {
        if (descriptor == null) {
            descriptor = getDescriptor0();
        }
        return descriptor;
    }

    private String getDescriptor0() {
        if (CollectionUtils.isEmpty(parameterTypes)) {
            return "()" + returnType.getDescriptor();
        }
        String parameterTypesDescriptor = StringKit.join("", parameterTypes, BDescribable::getDescriptor);
        return "(" + parameterTypesDescriptor + ")" + returnType.getDescriptor();
    }

    @Override
    public String getSignature() {
        if (signature == null) {
            signature = getSignature0();
        }
        return signature;
    }

    private String getSignature0() {
        String typeVariablesDeclaration = CollectionUtils.isEmpty(typeVariables) ? "" :
                ("<" + StringKit.join("", typeVariables, BTypeVariable::getDeclaration) + ">");
        String parameterTypesSignature = CollectionUtils.isEmpty(parameterTypes) ? "" :
                StringKit.join("", parameterTypes, BDescribable::getSignature);
        return typeVariablesDeclaration + "(" + parameterTypesSignature + ")" + returnType.getSignature();
    }
}
