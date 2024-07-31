package xyz.fslabo.common.reflect;

import java.lang.reflect.*;
import java.util.Objects;

interface TypePattern {

    static TypePattern defaultPattern() {
        return TypePatternImpl.INSTANCE;
    }

    default boolean matches(Type pattern, Type matched) {
        if (Objects.equals(pattern, matched)) {
            return true;
        }
        if (pattern instanceof Class<?>) {
            if (matched instanceof Class<?>) {
                return matchesClassToClass((Class<?>) pattern, (Class<?>) matched);
            }
            if (matched instanceof ParameterizedType) {
                return matchesClassToParameterized((Class<?>) pattern, (ParameterizedType) matched);
            }
            if (matched instanceof WildcardType) {
                return matchesClassToWildcard((Class<?>) pattern, (WildcardType) matched);
            }
            if (matched instanceof TypeVariable<?>) {
                return matchesClassToTypeVariable((Class<?>) pattern, (TypeVariable<?>) matched);
            }
            if (matched instanceof GenericArrayType) {
                return matchesClassToGenericArray((Class<?>) pattern, (GenericArrayType) matched);
            }
            return false;
        }
        if (pattern instanceof ParameterizedType) {
            if (matched instanceof Class<?>) {
                return matchesParameterizedToClass((ParameterizedType) pattern, (Class<?>) matched);
            }
            if (matched instanceof ParameterizedType) {
                return matchesParameterizedToParameterized((ParameterizedType) pattern, (ParameterizedType) matched);
            }
            if (matched instanceof WildcardType) {
                return matchesParameterizedToWildcard((ParameterizedType) pattern, (WildcardType) matched);
            }
            if (matched instanceof TypeVariable<?>) {
                return matchesParameterizedToTypeVariable((ParameterizedType) pattern, (TypeVariable<?>) matched);
            }
            if (matched instanceof GenericArrayType) {
                return matchesParameterizedToGenericArray((ParameterizedType) pattern, (GenericArrayType) matched);
            }
            return false;
        }
        if (pattern instanceof WildcardType) {
            if (matched instanceof Class<?>) {
                return matchesWildcardToClass((WildcardType) pattern, (Class<?>) matched);
            }
            if (matched instanceof ParameterizedType) {
                return matchesWildcardToParameterized((WildcardType) pattern, (ParameterizedType) matched);
            }
            if (matched instanceof WildcardType) {
                return matchesWildcardToWildcard((WildcardType) pattern, (WildcardType) matched);
            }
            if (matched instanceof TypeVariable<?>) {
                return matchesWildcardToTypeVariable((WildcardType) pattern, (TypeVariable<?>) matched);
            }
            if (matched instanceof GenericArrayType) {
                return matchesWildcardToGenericArray((WildcardType) pattern, (GenericArrayType) matched);
            }
            return false;
        }
        if (pattern instanceof TypeVariable<?>) {
            if (matched instanceof Class<?>) {
                return matchesTypeVariableToClass((TypeVariable<?>) pattern, (Class<?>) matched);
            }
            if (matched instanceof ParameterizedType) {
                return matchesTypeVariableToParameterized((TypeVariable<?>) pattern, (ParameterizedType) matched);
            }
            if (matched instanceof WildcardType) {
                return matchesTypeVariableToWildcard((TypeVariable<?>) pattern, (WildcardType) matched);
            }
            if (matched instanceof TypeVariable<?>) {
                return matchesTypeVariableToTypeVariable((TypeVariable<?>) pattern, (TypeVariable<?>) matched);
            }
            if (matched instanceof GenericArrayType) {
                return matchesTypeVariableToGenericArray((TypeVariable<?>) pattern, (GenericArrayType) matched);
            }
            return false;
        }
        if (pattern instanceof GenericArrayType) {
            if (matched instanceof Class<?>) {
                return matchesGenericArrayToClass((GenericArrayType) pattern, (Class<?>) matched);
            }
            if (matched instanceof ParameterizedType) {
                return matchesGenericArrayToParameterized((GenericArrayType) pattern, (ParameterizedType) matched);
            }
            if (matched instanceof WildcardType) {
                return matchesGenericArrayToWildcard((GenericArrayType) pattern, (WildcardType) matched);
            }
            if (matched instanceof TypeVariable<?>) {
                return matchesGenericArrayToTypeVariable((GenericArrayType) pattern, (TypeVariable<?>) matched);
            }
            if (matched instanceof GenericArrayType) {
                return matchesGenericArrayToGenericArray((GenericArrayType) pattern, (GenericArrayType) matched);
            }
            return false;
        }
        return false;
    }

    boolean matchesClassToClass(Class<?> pattern, Class<?> matched);

    boolean matchesClassToParameterized(Class<?> pattern, ParameterizedType matched);

    boolean matchesClassToWildcard(Class<?> pattern, WildcardType matched);

    boolean matchesClassToTypeVariable(Class<?> pattern, TypeVariable<?> matched);

    boolean matchesClassToGenericArray(Class<?> pattern, GenericArrayType matched);

    boolean matchesParameterizedToClass(ParameterizedType pattern, Class<?> matched);

    boolean matchesParameterizedToParameterized(ParameterizedType pattern, ParameterizedType matched);

    boolean matchesParameterizedToWildcard(ParameterizedType pattern, WildcardType matched);

    boolean matchesParameterizedToTypeVariable(ParameterizedType pattern, TypeVariable<?> matched);

    boolean matchesParameterizedToGenericArray(ParameterizedType pattern, GenericArrayType matched);

    boolean matchesWildcardToClass(WildcardType pattern, Class<?> matched);

    boolean matchesWildcardToParameterized(WildcardType pattern, ParameterizedType matched);

    boolean matchesWildcardToWildcard(WildcardType pattern, WildcardType matched);

    boolean matchesWildcardToTypeVariable(WildcardType pattern, TypeVariable<?> matched);

    boolean matchesWildcardToGenericArray(WildcardType pattern, GenericArrayType matched);

    boolean matchesTypeVariableToClass(TypeVariable<?> pattern, Class<?> matched);

    boolean matchesTypeVariableToParameterized(TypeVariable<?> pattern, ParameterizedType matched);

    boolean matchesTypeVariableToWildcard(TypeVariable<?> pattern, WildcardType matched);

    boolean matchesTypeVariableToTypeVariable(TypeVariable<?> pattern, TypeVariable<?> matched);

    boolean matchesTypeVariableToGenericArray(TypeVariable<?> pattern, GenericArrayType matched);

    boolean matchesGenericArrayToClass(GenericArrayType pattern, Class<?> matched);

    boolean matchesGenericArrayToParameterized(GenericArrayType pattern, ParameterizedType matched);

    boolean matchesGenericArrayToWildcard(GenericArrayType pattern, WildcardType matched);

    boolean matchesGenericArrayToTypeVariable(GenericArrayType pattern, TypeVariable<?> matched);

    boolean matchesGenericArrayToGenericArray(GenericArrayType pattern, GenericArrayType matched);

    default boolean isAssignable(Type assigned, Type assignee) {
        if (Objects.equals(assigned, assignee)) {
            return true;
        }
        if (assigned instanceof Class<?>) {
            if (assignee instanceof Class<?>) {
                return isAssignableClassToClass((Class<?>) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableClassToParameterized((Class<?>) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableClassToWildcard((Class<?>) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableClassToTypeVariable((Class<?>) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableClassToGenericArray((Class<?>) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        if (assigned instanceof ParameterizedType) {
            if (assignee instanceof Class<?>) {
                return isAssignableParameterizedToClass((ParameterizedType) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableParameterizedToParameterized((ParameterizedType) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableParameterizedToWildcard((ParameterizedType) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableParameterizedToTypeVariable((ParameterizedType) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableParameterizedToGenericArray((ParameterizedType) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        if (assigned instanceof WildcardType) {
            if (assignee instanceof Class<?>) {
                return isAssignableWildcardToClass((WildcardType) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableWildcardToParameterized((WildcardType) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableWildcardToWildcard((WildcardType) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableWildcardToTypeVariable((WildcardType) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableWildcardToGenericArray((WildcardType) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        if (assigned instanceof TypeVariable<?>) {
            if (assignee instanceof Class<?>) {
                return isAssignableTypeVariableToClass((TypeVariable<?>) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableTypeVariableToParameterized((TypeVariable<?>) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableTypeVariableToWildcard((TypeVariable<?>) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableTypeVariableToTypeVariable((TypeVariable<?>) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableTypeVariableToGenericArray((TypeVariable<?>) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        if (assigned instanceof GenericArrayType) {
            if (assignee instanceof Class<?>) {
                return isAssignableGenericArrayToClass((GenericArrayType) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableGenericArrayToParameterized((GenericArrayType) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableGenericArrayToWildcard((GenericArrayType) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableGenericArrayToTypeVariable((GenericArrayType) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableGenericArrayToGenericArray((GenericArrayType) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        return false;
    }

    boolean isAssignableClassToClass(Class<?> assigned, Class<?> assignee);

    boolean isAssignableClassToParameterized(Class<?> assigned, ParameterizedType assignee);

    boolean isAssignableClassToWildcard(Class<?> assigned, WildcardType assignee);

    boolean isAssignableClassToTypeVariable(Class<?> assigned, TypeVariable<?> assignee);

    boolean isAssignableClassToGenericArray(Class<?> assigned, GenericArrayType assignee);

    boolean isAssignableParameterizedToClass(ParameterizedType assigned, Class<?> assignee);

    boolean isAssignableParameterizedToParameterized(ParameterizedType assigned, ParameterizedType assignee);

    boolean isAssignableParameterizedToWildcard(ParameterizedType assigned, WildcardType assignee);

    boolean isAssignableParameterizedToTypeVariable(ParameterizedType assigned, TypeVariable<?> assignee);

    boolean isAssignableParameterizedToGenericArray(ParameterizedType assigned, GenericArrayType assignee);

    boolean isAssignableWildcardToClass(WildcardType assigned, Class<?> assignee);

    boolean isAssignableWildcardToParameterized(WildcardType assigned, ParameterizedType assignee);

    boolean isAssignableWildcardToWildcard(WildcardType assigned, WildcardType assignee);

    boolean isAssignableWildcardToTypeVariable(WildcardType assigned, TypeVariable<?> assignee);

    boolean isAssignableWildcardToGenericArray(WildcardType assigned, GenericArrayType assignee);

    boolean isAssignableTypeVariableToClass(TypeVariable<?> assigned, Class<?> assignee);

    boolean isAssignableTypeVariableToParameterized(TypeVariable<?> assigned, ParameterizedType assignee);

    boolean isAssignableTypeVariableToWildcard(TypeVariable<?> assigned, WildcardType assignee);

    boolean isAssignableTypeVariableToTypeVariable(TypeVariable<?> assigned, TypeVariable<?> assignee);

    boolean isAssignableTypeVariableToGenericArray(TypeVariable<?> assigned, GenericArrayType assignee);

    boolean isAssignableGenericArrayToClass(GenericArrayType assigned, Class<?> assignee);

    boolean isAssignableGenericArrayToParameterized(GenericArrayType assigned, ParameterizedType assignee);

    boolean isAssignableGenericArrayToWildcard(GenericArrayType assigned, WildcardType assignee);

    boolean isAssignableGenericArrayToTypeVariable(GenericArrayType assigned, TypeVariable<?> assignee);

    boolean isAssignableGenericArrayToGenericArray(GenericArrayType assigned, GenericArrayType assignee);
}
