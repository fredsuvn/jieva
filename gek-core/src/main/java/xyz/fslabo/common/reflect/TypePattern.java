package xyz.fslabo.common.reflect;

import xyz.fslabo.common.base.Jie;

import java.lang.reflect.*;
import java.util.Objects;

final class TypePattern {

    private static final Type[] EMPTY_TYPE_ARRAY = {};

    static boolean matches(Type pattern, Type matched) {
        if (Objects.equals(pattern, matched)) {
            return true;
        }
        if (pattern instanceof Class<?>) {
            Class<?> p = (Class<?>) pattern;
            if (matched instanceof Class<?>) {
                return false;
            }
            if (matched instanceof ParameterizedType) {
                ParameterizedType m = (ParameterizedType) matched;
                Class<?> mRaw = (Class<?>) m.getRawType();
                if (!Objects.equals(p, mRaw)) {
                    return false;
                }
                Type[] args = m.getActualTypeArguments();
                return matchesTypeArgs(EMPTY_TYPE_ARRAY, args);
            }
            if (matched instanceof TypeVariable<?>) {
                return false;
            }
            if (matched instanceof WildcardType) {
                return matchesTypeWildcard(pattern, (WildcardType) matched);
            }
            if (matched instanceof GenericArrayType) {
                GenericArrayType m = (GenericArrayType) matched;
                if (!p.isArray()) {
                    return false;
                }
                Type mComponent = m.getGenericComponentType();
                Type pComponent = p.getComponentType();
                return matches(pComponent, mComponent);
            }
            return false;
        }
        if (pattern instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) pattern;
            if (matched instanceof Class<?>) {
                Class<?> m = (Class<?>) matched;
                Class<?> pRaw = (Class<?>) p.getRawType();
                if (!Objects.equals(pRaw, m)) {
                    return false;
                }
                Type[] args = p.getActualTypeArguments();
                return matchesTypeArgs(args, EMPTY_TYPE_ARRAY);
            }
            if (matched instanceof ParameterizedType) {
                ParameterizedType m = (ParameterizedType) matched;
                Class<?> pRaw = (Class<?>) p.getRawType();
                Class<?> mRaw = (Class<?>) m.getRawType();
                if (!Objects.equals(pRaw, mRaw)) {
                    return false;
                }
                Type[] pArgs = Jie.orDefault(p.getActualTypeArguments(), EMPTY_TYPE_ARRAY);
                Type[] mArgs = Jie.orDefault(m.getActualTypeArguments(), EMPTY_TYPE_ARRAY);
                return matchesTypeArgs(pArgs, mArgs);
            }
            if (matched instanceof TypeVariable<?>) {
                return false;
            }
            if (matched instanceof WildcardType) {
                return matchesTypeWildcard(pattern, (WildcardType) matched);
            }
            if (matched instanceof GenericArrayType) {
                return false;
            }
            return false;
        }
        if (pattern instanceof TypeVariable<?>) {
            TypeVariable<?> p = (TypeVariable<?>) pattern;
            if (matched instanceof Class<?>) {
                return matchesTypeVariable(p, matched);
            }
            if (matched instanceof ParameterizedType) {
                return matchesTypeVariable(p, matched);
            }
            if (matched instanceof TypeVariable<?>) {
                TypeVariable<?> m = (TypeVariable<?>) matched;
                Type[] pBounds = JieReflect.getUpperBounds(p);
                Type[] mBounds = JieReflect.getUpperBounds(m);
                for (Type pu : pBounds) {
                    boolean ok = false;
                    for (Type mu : mBounds) {
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
            if (matched instanceof WildcardType) {
                WildcardType m = (WildcardType) matched;
                Type mUpper = JieReflect.getUpperBound(m);
                Type[] pUpper = JieReflect.getUpperBounds(p);
                for (Type pu : pUpper) {
                    if (!isAssignable(pu, mUpper)) {
                        return false;
                    }
                }
                return true;
            }
            if (matched instanceof GenericArrayType) {
                return matchesTypeVariable(p, matched);
            }
            return false;
        }
        if (pattern instanceof WildcardType) {
            WildcardType p = (WildcardType) pattern;
            if (matched instanceof Class<?>) {
                return matchesWildcardType(p, matched);
            }
            if (matched instanceof ParameterizedType) {
                return matchesWildcardType(p, matched);
            }
            if (matched instanceof TypeVariable<?>) {
                TypeVariable<?> m = (TypeVariable<?>) matched;
                Type pLower = JieReflect.getLowerBound(p);
                if (pLower != null) {
                    return false;
                }
                Type[] mUppers = JieReflect.getUpperBounds(m);
                Type pUpper = JieReflect.getUpperBound(p);
                for (Type mu : mUppers) {
                    if (isAssignable(pUpper, mu)) {
                        return true;
                    }
                }
                return false;
            }
            if (matched instanceof WildcardType) {
                WildcardType m = (WildcardType) matched;
                Type pUpper = JieReflect.getUpperBound(p);
                Type mUpper = JieReflect.getUpperBound(m);
                if (!isAssignable(pUpper, mUpper)) {
                    return false;
                }
                Type pLower = JieReflect.getLowerBound(p);
                if (pLower == null) {
                    return true;
                }
                Type mLower = JieReflect.getLowerBound(m);
                if (mLower == null) {
                    return false;
                }
                return isAssignable(mLower, pLower);
            }
            if (matched instanceof GenericArrayType) {
                return matchesWildcardType(p, matched);
            }
            return false;
        }
        if (pattern instanceof GenericArrayType) {
            GenericArrayType p = (GenericArrayType) pattern;
            if (matched instanceof Class<?>) {
                Class<?> m = (Class<?>) matched;
                if (!m.isArray()) {
                    return false;
                }
                Type pComponent = p.getGenericComponentType();
                Type mComponent = m.getComponentType();
                return matches(pComponent, mComponent);
            }
            if (matched instanceof ParameterizedType) {
                return false;
            }
            if (matched instanceof TypeVariable<?>) {
                return false;
            }
            if (matched instanceof WildcardType) {
                return matchesTypeWildcard(pattern, (WildcardType) matched);
            }
            if (matched instanceof GenericArrayType) {
                GenericArrayType m = (GenericArrayType) matched;
                Type pComponent = p.getGenericComponentType();
                Type mComponent = m.getGenericComponentType();
                return matches(pComponent, mComponent);
            }
            return false;
        }
        return false;
    }

    private static boolean matchesTypeArgs(Type[] pArgs, Type[] mArgs) {
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

    private static boolean matchesTypeWildcard(Type pattern, WildcardType matched) {
        Type mLower = JieReflect.getLowerBound(matched);
        if (mLower == null) {
            return false;
        }
        Type mUpper = JieReflect.getUpperBound(matched);
        return matches(pattern, mLower) && matches(pattern, mUpper);
    }

    private static boolean matchesTypeVariable(TypeVariable<?> pattern, Type type) {
        Type[] upperBounds = JieReflect.getUpperBounds(pattern);
        for (Type ub : upperBounds) {
            if (!isAssignable(ub, type)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesWildcardType(WildcardType pattern, Type type) {
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

   static boolean isAssignable(Type targetType, Type sourceType) {
        if (Objects.equals(targetType, sourceType) || Objects.equals(targetType, Object.class)) {
            return true;
        }
        if ((targetType instanceof WildcardType)
            || (sourceType instanceof WildcardType)) {
            return false;
        }
        if (targetType instanceof Class<?>) {
            Class<?> c1 = (Class<?>) targetType;
            if (sourceType instanceof Class<?>) {
                Class<?> c2 = (Class<?>) sourceType;
                return isAssignable2(c1, c2);
            }
            if (sourceType instanceof ParameterizedType) {
                ParameterizedType p2 = (ParameterizedType) sourceType;
                Class<?> r2 = getRawType(p2);
                if (r2 == null) {
                    return false;
                }
                return isAssignable2(c1, r2);
            }
            if (sourceType instanceof TypeVariable<?>) {
                TypeVariable<?> v2 = (TypeVariable<?>) sourceType;
                Type[] bounds = v2.getBounds();
                for (Type bound : bounds) {
                    if (isAssignable(targetType, bound)) {
                        return true;
                    }
                }
                return false;
            }
            if (sourceType instanceof GenericArrayType) {
                if (!c1.isArray()) {
                    return false;
                }
                GenericArrayType g2 = (GenericArrayType) sourceType;
                Class<?> gc2 = getRawType(g2.getGenericComponentType());
                if (gc2 == null) {
                    return false;
                }
                return isAssignable2(c1.getComponentType(), gc2);
            }
            return false;
        }
        if (targetType instanceof ParameterizedType) {
            ParameterizedType p1 = (ParameterizedType) targetType;
            if (sourceType instanceof Class<?>) {
                ParameterizedType p2 = getGenericSuperType(sourceType, (Class<?>) p1.getRawType());
                if (p2 == null) {
                    return false;
                }
                return isAssignable(p1, p2);
            }
            if (sourceType instanceof ParameterizedType) {
                ParameterizedType p2 = (ParameterizedType) sourceType;
                if (Objects.equals(p1.getRawType(), p2.getRawType())) {
                    Type[] a1 = p1.getActualTypeArguments();
                    Type[] a2 = p2.getActualTypeArguments();
                    if (a1 == null || a2 == null || a1.length != a2.length) {
                        return false;
                    }
                    for (int i = 0; i < a1.length; i++) {
                        Type at1 = a1[i];
                        Type at2 = a2[i];
                        if (!isAssignableFromForParameterizedType(at1, at2)) {
                            return false;
                        }
                    }
                    return true;
                }
                ParameterizedType sp2 = getGenericSuperType(p2, (Class<?>) p1.getRawType());
                if (sp2 == null) {
                    return false;
                }
                return isAssignable(p1, sp2);
            }
            if (sourceType instanceof TypeVariable<?>) {
                TypeVariable<?> v2 = (TypeVariable<?>) sourceType;
                Type[] bounds = v2.getBounds();
                for (Type bound : bounds) {
                    if (isAssignable(targetType, bound)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> v1 = (TypeVariable<?>) targetType;
            if (sourceType instanceof TypeVariable<?>) {
                TypeVariable<?> v2 = (TypeVariable<?>) sourceType;
                Type[] bounds = v2.getBounds();
                for (Type bound : bounds) {
                    if (isAssignable(targetType, bound)) {
                        return true;
                    }
                }
                return false;
            }
        }
        if (targetType instanceof GenericArrayType) {
            GenericArrayType g1 = (GenericArrayType) targetType;
            if (sourceType instanceof Class<?>) {
                Class<?> c2 = (Class<?>) sourceType;
                if (!c2.isArray()) {
                    return false;
                }
                Class<?> tc1 = getRawType(g1.getGenericComponentType());
                if (tc1 == null) {
                    return false;
                }
                Class<?> tc2 = c2.getComponentType();
                return isAssignable2(tc1, tc2);
            }
            if (sourceType instanceof GenericArrayType) {
                GenericArrayType g2 = (GenericArrayType) sourceType;
                Type tc1 = g1.getGenericComponentType();
                Type tc2 = g2.getGenericComponentType();
                return isAssignable(tc1, tc2);
            }
            return false;
        }
        return false;
    }

    private static boolean isAssignableFromForParameterizedType(Type t1, Type t2) {
        if (Objects.equals(t1, t2)) {
            return true;
        }
        if (t1 instanceof WildcardType) {
            WildcardType w1 = (WildcardType) t1;
            Type upperBound = getUpperBound(w1);
            //? extends
            if (upperBound != null) {
                if (t2 instanceof WildcardType) {
                    WildcardType w2 = (WildcardType) t2;
                    Type upperBound2 = getUpperBound(w2);
                    //? extends
                    if (upperBound2 != null) {
                        return isAssignable(upperBound, upperBound2);
                    }
                    return false;
                }
                return isAssignable(upperBound, t2);
            }
            Type lowerBound = getLowerBound(w1);
            //? super
            if (lowerBound != null) {
                if (t2 instanceof WildcardType) {
                    WildcardType w2 = (WildcardType) t2;
                    Type lowerBound2 = getLowerBound(w2);
                    //? super
                    if (lowerBound2 != null) {
                        return isAssignable(lowerBound2, lowerBound);
                    }
                    return false;
                }
                return isAssignable(t2, lowerBound);
            }
        }
        return false;
    }
}
