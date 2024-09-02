package xyz.fslabo.common.reflect;

import java.lang.reflect.*;
import java.util.Objects;

final class TypePatternImpl implements TypePattern {

    static final TypePatternImpl INSTANCE = new TypePatternImpl();


    @Override
    public boolean isAssignableClassToClass(Class<?> assigned, Class<?> assignee) {
        return assigned.isAssignableFrom(assignee);
    }

    @Override
    public boolean isAssignableClassToParameterized(Class<?> assigned, ParameterizedType assignee) {
        Class<?> assigneeRaw = (Class<?>) assignee.getRawType();
        return assigned.isAssignableFrom(assigneeRaw);
    }

    @Override
    public boolean isAssignableClassToWildcard(Class<?> assigned, WildcardType assignee) {
        return isAssignableToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableClassToTypeVariable(Class<?> assigned, TypeVariable<?> assignee) {
        return isAssignableToTypeVariable(assigned, assignee);
    }

    @Override
    public boolean isAssignableClassToGenericArray(Class<?> assigned, GenericArrayType assignee) {
        if (!assigned.isArray()) {
            return false;
        }
        Type assigneeComponent = assignee.getGenericComponentType();
        if (assigneeComponent instanceof Class<?>) {
            return assigned.getComponentType().isAssignableFrom((Class<?>) assigneeComponent);
        }
        if (assigneeComponent instanceof ParameterizedType) {
            Class<?> assigneeComponentRaw = (Class<?>) ((ParameterizedType) assigneeComponent).getRawType();
            return assigned.getComponentType().isAssignableFrom(assigneeComponentRaw);
        }
        return false;
    }

    @Override
    public boolean isAssignableParameterizedToClass(ParameterizedType assigned, Class<?> assignee) {
        Class<?> assignedRaw = (Class<?>) assigned.getRawType();
        return assignedRaw.isAssignableFrom(assignee);
    }

    @Override
    public boolean isAssignableParameterizedToParameterized(ParameterizedType assigned, ParameterizedType assignee) {
        Class<?> assignedRaw = (Class<?>) assigned.getRawType();
        Class<?> assigneeRaw = (Class<?>) assignee.getRawType();
        if (!assignedRaw.isAssignableFrom(assigneeRaw)) {
            return false;
        }
        Type[] assignedArgs = assigned.getActualTypeArguments();
        Type[] assigneeArgs = assignee.getActualTypeArguments();
        if (assignedArgs.length != assigneeArgs.length) {
            return false;
        }
        for (int i = 0; i < assignedArgs.length; i++) {
            Type assignedArg = assignedArgs[i];
            Type assigneeArg = assigneeArgs[i];
            if (assignedArg instanceof WildcardType) {
                if (!isAssignable(assignedArg, assigneeArg)) {
                    return false;
                }
                continue;
            }
            if (!Objects.equals(assignedArg, assigneeArg)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAssignableParameterizedToWildcard(ParameterizedType assigned, WildcardType assignee) {
        return isAssignableToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableParameterizedToTypeVariable(ParameterizedType assigned, TypeVariable<?> assignee) {
        return isAssignableToTypeVariable(assigned, assignee);
    }

    @Override
    public boolean isAssignableParameterizedToGenericArray(ParameterizedType assigned, GenericArrayType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableWildcardToClass(WildcardType assigned, Class<?> assignee) {
        return false;
    }

    @Override
    public boolean isAssignableWildcardToParameterized(WildcardType assigned, ParameterizedType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableWildcardToWildcard(WildcardType assigned, WildcardType assignee) {
        Type assignedUpper = JieReflect.getUpperBound(assigned);
        Type assigneeUpper = JieReflect.getUpperBound(assignee);
        if (!isAssignable(assignedUpper, assigneeUpper)) {
            return false;
        }
        Type assignedLower = JieReflect.getLowerBound(assigned);
        if (assignedLower == null) {
            return true;
        }
        Type assigneeLower = JieReflect.getLowerBound(assignee);
        if (assigneeLower == null) {
            return false;
        }
        return isAssignable(assigneeLower, assignedLower);
    }

    @Override
    public boolean isAssignableWildcardToTypeVariable(WildcardType assigned, TypeVariable<?> assignee) {
        Type assignedLower = JieReflect.getLowerBound(assigned);
        if (assignedLower != null) {
            return false;
        }
        Type assignedUpper = JieReflect.getUpperBound(assigned);
        if (isAssignable(assignedUpper, assignee)) {
            return true;
        }
        Type[] assigneeUppers = assignee.getBounds();
        for (Type assigneeUpper : assigneeUppers) {
            if (isAssignable(assignedUpper, assigneeUpper)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAssignableWildcardToGenericArray(WildcardType assigned, GenericArrayType assignee) {
        return isAssignableWildcard(assigned, assignee);
    }

    private boolean isAssignableWildcard(WildcardType assigned, Type assignee) {
        Type upper = JieReflect.getUpperBound(assigned);
        if (!isAssignable(upper, assignee)) {
            return false;
        }
        Type lower = JieReflect.getLowerBound(assigned);
        if (lower == null) {
            return true;
        }
        return isAssignable(assignee, lower);
    }

    @Override
    public boolean isAssignableTypeVariableToClass(TypeVariable<?> assigned, Class<?> assignee) {
        return false;
    }

    @Override
    public boolean isAssignableTypeVariableToParameterized(TypeVariable<?> assigned, ParameterizedType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableTypeVariableToWildcard(TypeVariable<?> assigned, WildcardType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableTypeVariableToTypeVariable(TypeVariable<?> assigned, TypeVariable<?> assignee) {
        Type[] assigneeUppers = assignee.getBounds();
        for (Type assigneeUpper : assigneeUppers) {
            if (Objects.equals(assigneeUpper, assigned)) {
                return true;
            }
            if (assigneeUpper instanceof TypeVariable<?>) {
                if (isAssignableTypeVariableToTypeVariable(assigned, (TypeVariable<?>) assigneeUpper)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isAssignableTypeVariableToGenericArray(TypeVariable<?> assigned, GenericArrayType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableGenericArrayToClass(GenericArrayType assigned, Class<?> assignee) {
        if (!assignee.isArray()) {
            return false;
        }
        Type assignedComponent = assigned.getGenericComponentType();
        if (assignedComponent instanceof Class<?>) {
            return ((Class<?>) assignedComponent).isAssignableFrom(assignee.getComponentType());
        }
        if (assignedComponent instanceof ParameterizedType) {
            Class<?> assignedComponentRaw = (Class<?>) ((ParameterizedType) assignedComponent).getRawType();
            return assignedComponentRaw.isAssignableFrom(assignee.getComponentType());
        }
        return false;
    }

    @Override
    public boolean isAssignableGenericArrayToParameterized(GenericArrayType assigned, ParameterizedType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableGenericArrayToWildcard(GenericArrayType assigned, WildcardType assignee) {
        return isAssignableToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableGenericArrayToTypeVariable(GenericArrayType assigned, TypeVariable<?> assignee) {
        return isAssignableToTypeVariable(assigned, assignee);
    }

    @Override
    public boolean isAssignableGenericArrayToGenericArray(GenericArrayType assigned, GenericArrayType assignee) {
        Type assignedComponent = assigned.getGenericComponentType();
        Type assigneeComponent = assignee.getGenericComponentType();
        return isAssignable(assignedComponent, assigneeComponent);
    }

    public boolean isAssignableToWildcard(Type assigned, WildcardType assignee) {
        Type assigneeUpper = JieReflect.getUpperBound(assignee);
        return isAssignable(assigned, assigneeUpper);
    }

    public boolean isAssignableToTypeVariable(Type assigned, TypeVariable<?> assignee) {
        Type[] assigneeUppers = assignee.getBounds();
        for (Type assigneeUpper : assigneeUppers) {
            if (isAssignable(assigned, assigneeUpper)) {
                return true;
            }
        }
        return false;
    }
}
