package xyz.fslabo.common.reflect;

import xyz.fslabo.common.base.Jie;

import java.lang.reflect.*;
import java.util.Objects;

final class TypePatternImpl implements TypePattern {

    private static final Type[] EMPTY_TYPE_ARRAY = {};
    static final TypePatternImpl INSTANCE = new TypePatternImpl();

    @Override
    public boolean matchesClassToClass(Class<?> pattern, Class<?> matched) {
        return false;
    }

    @Override
    public boolean matchesClassToParameterized(Class<?> pattern, ParameterizedType matched) {
        return false;
    }

    @Override
    public boolean matchesClassToWildcard(Class<?> pattern, WildcardType matched) {
        return false;
    }

    @Override
    public boolean matchesClassToTypeVariable(Class<?> pattern, TypeVariable<?> matched) {
        return false;
    }

    @Override
    public boolean matchesClassToGenericArray(Class<?> pattern, GenericArrayType matched) {
        return false;
    }

    @Override
    public boolean matchesParameterizedToClass(ParameterizedType pattern, Class<?> matched) {
        return false;
    }

    @Override
    public boolean matchesParameterizedToParameterized(ParameterizedType pattern, ParameterizedType matched) {
        Class<?> pRaw = (Class<?>) pattern.getRawType();
        Class<?> mRaw = (Class<?>) matched.getRawType();
        if (!Objects.equals(pRaw, mRaw)) {
            return false;
        }
        Type[] pArgs = Jie.orDefault(pattern.getActualTypeArguments(), EMPTY_TYPE_ARRAY);
        Type[] mArgs = Jie.orDefault(matched.getActualTypeArguments(), EMPTY_TYPE_ARRAY);
        return matchesTypeArgs(pArgs, mArgs);
    }

    @Override
    public boolean matchesParameterizedToWildcard(ParameterizedType pattern, WildcardType matched) {
        return false;
    }

    @Override
    public boolean matchesParameterizedToTypeVariable(ParameterizedType pattern, TypeVariable<?> matched) {
        return false;
    }

    @Override
    public boolean matchesParameterizedToGenericArray(ParameterizedType pattern, GenericArrayType matched) {
        return false;
    }

    @Override
    public boolean matchesWildcardToClass(Wildcard pattern, Class<?> matched) {
        return matchesWildcardType(pattern, matched);
    }

    @Override
    public boolean matchesWildcardToParameterized(Wildcard pattern, ParameterizedType matched) {
        return matchesWildcardType(pattern, matched);
    }

    @Override
    public boolean matchesWildcardToWildcard(Wildcard pattern, WildcardType matched) {
        Type pUpper = JieReflect.getUpperBound(pattern);
        Type mUpper = JieReflect.getUpperBound(matched);
        if (!isAssignable(pUpper, mUpper)) {
            return false;
        }
        Type pLower = JieReflect.getLowerBound(pattern);
        if (pLower == null) {
            return true;
        }
        Type mLower = JieReflect.getLowerBound(matched);
        if (mLower == null) {
            return false;
        }
        return isAssignable(mLower, pLower);
    }

    @Override
    public boolean matchesWildcardToTypeVariable(Wildcard pattern, TypeVariable<?> matched) {
        Type pLower = JieReflect.getLowerBound(pattern);
        if (pLower != null) {
            return false;
        }
        Type[] mUppers = JieReflect.getUpperBounds(matched);
        Type pUpper = JieReflect.getUpperBound(pattern);
        for (Type mu : mUppers) {
            if (isAssignable(pUpper, mu)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean matchesWildcardToGenericArray(Wildcard pattern, GenericArrayType matched) {
        return matchesWildcardType(pattern, matched);
    }

    @Override
    public boolean matchesTypeVariableToClass(TypeVariable<?> pattern, Class<?> matched) {
        return matchesTypeVariable(pattern, matched);
    }

    @Override
    public boolean matchesTypeVariableToParameterized(TypeVariable<?> pattern, ParameterizedType matched) {
        return matchesTypeVariable(pattern, matched);
    }

    @Override
    public boolean matchesTypeVariableToWildcard(TypeVariable<?> pattern, WildcardType matched) {
        Type[] pUppers = JieReflect.getUpperBounds(pattern);
        Type mUpper = JieReflect.getUpperBound(matched);
        for (Type pu : pUppers) {
            if (!isAssignable(pu, mUpper)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean matchesTypeVariableToTypeVariable(TypeVariable<?> pattern, TypeVariable<?> matched) {
        Type[] pUppers = JieReflect.getUpperBounds(pattern);
        Type[] mUppers = JieReflect.getUpperBounds(matched);
        for (Type pu : pUppers) {
            boolean ok = false;
            for (Type mu : mUppers) {
                if (isAssignable(pu, mu)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean matchesTypeVariableToGenericArray(TypeVariable<?> pattern, GenericArrayType matched) {
        return matchesTypeVariable(pattern, matched);
    }

    @Override
    public boolean matchesGenericArrayToClass(GenericArrayType pattern, Class<?> matched) {
        return false;
    }

    @Override
    public boolean matchesGenericArrayToParameterized(GenericArrayType pattern, ParameterizedType matched) {
        return false;
    }

    @Override
    public boolean matchesGenericArrayToWildcard(GenericArrayType pattern, WildcardType matched) {
        return false;
    }

    @Override
    public boolean matchesGenericArrayToTypeVariable(GenericArrayType pattern, TypeVariable<?> matched) {
        return false;
    }

    @Override
    public boolean matchesGenericArrayToGenericArray(GenericArrayType pattern, GenericArrayType matched) {
        Type pComponent = pattern.getGenericComponentType();
        Type mComponent = matched.getGenericComponentType();
        return matches(pComponent, mComponent);
    }

    private boolean matchesTypeArgs(Type[] pArgs, Type[] mArgs) {
        int size = Math.max(pArgs.length, mArgs.length);
        for (int i = 0; i < size; i++) {
            Type p = i < pArgs.length ? pArgs[i] : JieType.questionMark();
            Type m = i < mArgs.length ? mArgs[i] : JieType.questionMark();
            if (!matches(p, m)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesWildcardType(WildcardType pattern, Type type) {
        Type upper = JieReflect.getUpperBound(pattern);
        if (!isAssignable(upper, type)) {
            return false;
        }
        Type lower = JieReflect.getLowerBound(pattern);
        if (lower == null) {
            return true;
        }
        return isAssignable(type, lower);
    }

    private boolean matchesTypeVariable(TypeVariable<?> pattern, Type type) {
        Type[] pUppers = JieReflect.getUpperBounds(pattern);
        for (Type ub : pUppers) {
            if (!isAssignable(ub, type)) {
                return false;
            }
        }
        return true;
    }

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
        Type assigneeUpper = JieReflect.getUpperBound(assignee);
        return isAssignable(assigned, assigneeUpper);
    }

    @Override
    public boolean isAssignableClassToTypeVariable(Class<?> assigned, TypeVariable<?> assignee) {
        Type[] assigneeUppers = JieReflect.getUpperBounds(assignee);
        for (Type assigneeUpper : assigneeUppers) {
            if (!isAssignable(assigned, assigneeUpper)) {
                return false;
            }
        }
        return true;
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
        Type[] assignedArgs = Jie.orDefault(assigned.getActualTypeArguments(), EMPTY_TYPE_ARRAY);
        Type[] assigneeArgs = Jie.orDefault(assignee.getActualTypeArguments(), EMPTY_TYPE_ARRAY);
        return matchesTypeArgs(assignedArgs, assigneeArgs);
    }

    @Override
    public boolean isAssignableParameterizedToWildcard(ParameterizedType assigned, WildcardType assignee) {
        Type assigneeUpper = JieReflect.getUpperBound(assignee);
        return isAssignable(assigned, assigneeUpper);
    }

    @Override
    public boolean isAssignableParameterizedToTypeVariable(ParameterizedType assigned, TypeVariable<?> assignee) {
        Type[] assigneeUppers = JieReflect.getUpperBounds(assignee);
        for (Type assigneeUpper : assigneeUppers) {
            if (!isAssignable(assigned, assigneeUpper)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAssignableParameterizedToGenericArray(ParameterizedType assigned, GenericArrayType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableWildcardToClass(Wildcard assigned, Class<?> assignee) {
        return matchesWildcardToClass(assigned, assignee);
    }

    @Override
    public boolean isAssignableWildcardToParameterized(Wildcard assigned, ParameterizedType assignee) {
        return matchesWildcardToParameterized(assigned, assignee);
    }

    @Override
    public boolean isAssignableWildcardToWildcard(Wildcard assigned, WildcardType assignee) {
        return matchesWildcardToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableWildcardToTypeVariable(Wildcard assigned, TypeVariable<?> assignee) {
        return matchesWildcardToTypeVariable(assigned, assignee);
    }

    @Override
    public boolean isAssignableWildcardToGenericArray(Wildcard assigned, GenericArrayType assignee) {
        return matchesWildcardToGenericArray(assigned, assignee);
    }

    @Override
    public boolean isAssignableTypeVariableToClass(TypeVariable<?> assigned, Class<?> assignee) {
        return matchesTypeVariableToClass(assigned, assignee);
    }

    @Override
    public boolean isAssignableTypeVariableToParameterized(TypeVariable<?> assigned, ParameterizedType assignee) {
        return matchesTypeVariableToParameterized(assigned, assignee);
    }

    @Override
    public boolean isAssignableTypeVariableToWildcard(TypeVariable<?> assigned, WildcardType assignee) {
        return matchesTypeVariableToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableTypeVariableToTypeVariable(TypeVariable<?> assigned, TypeVariable<?> assignee) {
        return matchesTypeVariableToTypeVariable(assigned, assignee);
    }

    @Override
    public boolean isAssignableTypeVariableToGenericArray(TypeVariable<?> assigned, GenericArrayType assignee) {
        return matchesTypeVariableToGenericArray(assigned, assignee);
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
        Type assigneeUpper = JieReflect.getUpperBound(assignee);
        return isAssignable(assigned, assigneeUpper);
    }

    @Override
    public boolean isAssignableGenericArrayToTypeVariable(GenericArrayType assigned, TypeVariable<?> assignee) {
        Type[] assigneeUppers = JieReflect.getUpperBounds(assignee);
        for (Type assigneeUpper : assigneeUppers) {
            if (!isAssignable(assigned, assigneeUpper)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAssignableGenericArrayToGenericArray(GenericArrayType assigned, GenericArrayType assignee) {
        Type assignedComponent = assigned.getGenericComponentType();
        Type assigneeComponent = assignee.getGenericComponentType();
        return isAssignable(assignedComponent, assigneeComponent);
    }
}
