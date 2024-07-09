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

     static boolean isAssignable(Type assigned, Type assignee) {
        if (Objects.equals(assigned, assignee) || Objects.equals(assigned, Object.class)) {
            return true;
        }
         if (assigned instanceof Class<?>) {
             Class<?> p = (Class<?>) assigned;
             if (assignee instanceof Class<?>) {
                 Class<?> m = (Class<?>) assignee;
                 return p.isAssignableFrom(m);
             }
             if (assignee instanceof ParameterizedType) {
                 ParameterizedType m = (ParameterizedType) assignee;
                 Class<?> mRaw = (Class<?>) m.getRawType();
                 //Note:
                 // List list = null;
                 // List<? extends String> list2 = null;
                 // list = list2;// true!
                 // list2 = list;// true!
                 return p.isAssignableFrom(mRaw);
             }
             if (assignee instanceof TypeVariable<?>) {
                 return matches(JieType.wildcard(p, null), assigned);
             }
             if (assignee instanceof WildcardType) {
                 return matches(JieType.wildcard(p, null), assigned);
             }
             if (assignee instanceof GenericArrayType) {
                 GenericArrayType m = (GenericArrayType) assignee;
                 if (!p.isArray()) {
                     return false;
                 }
                 Type mComponent = m.getGenericComponentType();
                 Type pComponent = p.getComponentType();
                 return matches(pComponent, mComponent);
             }
             return false;
         }
         if (assigned instanceof ParameterizedType) {
             ParameterizedType p = (ParameterizedType) assigned;
             if (assignee instanceof Class<?>) {
                 Class<?> m = (Class<?>) assignee;
                 Class<?> pRaw = (Class<?>) p.getRawType();
                 if (!Objects.equals(pRaw, m)) {
                     return false;
                 }
                 Type[] args = p.getActualTypeArguments();
                 return matchesTypeArgs(args, EMPTY_TYPE_ARRAY);
             }
             if (assignee instanceof ParameterizedType) {
                 ParameterizedType m = (ParameterizedType) assignee;
                 Class<?> pRaw = (Class<?>) p.getRawType();
                 Class<?> mRaw = (Class<?>) m.getRawType();
                 if (!Objects.equals(pRaw, mRaw)) {
                     return false;
                 }
                 Type[] pArgs = Jie.orDefault(p.getActualTypeArguments(), EMPTY_TYPE_ARRAY);
                 Type[] mArgs = Jie.orDefault(m.getActualTypeArguments(), EMPTY_TYPE_ARRAY);
                 return matchesTypeArgs(pArgs, mArgs);
             }
             if (assignee instanceof TypeVariable<?>) {
                 return false;
             }
             if (assignee instanceof WildcardType) {
                 return matchesTypeWildcard(assigned, (WildcardType) assignee);
             }
             if (assignee instanceof GenericArrayType) {
                 return false;
             }
             return false;
         }
         if (assigned instanceof TypeVariable<?>) {
             TypeVariable<?> p = (TypeVariable<?>) assigned;
             if (assignee instanceof Class<?>) {
                 return matchesTypeVariable(p, assignee);
             }
             if (assignee instanceof ParameterizedType) {
                 return matchesTypeVariable(p, assignee);
             }
             if (assignee instanceof TypeVariable<?>) {
                 TypeVariable<?> m = (TypeVariable<?>) assignee;
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
             if (assignee instanceof WildcardType) {
                 WildcardType m = (WildcardType) assignee;
                 Type mUpper = JieReflect.getUpperBound(m);
                 Type[] pUpper = JieReflect.getUpperBounds(p);
                 for (Type pu : pUpper) {
                     if (!isAssignable(pu, mUpper)) {
                         return false;
                     }
                 }
                 return true;
             }
             if (assignee instanceof GenericArrayType) {
                 return matchesTypeVariable(p, assignee);
             }
             return false;
         }
         if (assigned instanceof WildcardType) {
             WildcardType p = (WildcardType) assigned;
             if (assignee instanceof Class<?>) {
                 return matchesWildcardType(p, assignee);
             }
             if (assignee instanceof ParameterizedType) {
                 return matchesWildcardType(p, assignee);
             }
             if (assignee instanceof TypeVariable<?>) {
                 TypeVariable<?> m = (TypeVariable<?>) assignee;
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
             if (assignee instanceof WildcardType) {
                 WildcardType m = (WildcardType) assignee;
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
             if (assignee instanceof GenericArrayType) {
                 return matchesWildcardType(p, assignee);
             }
             return false;
         }
         if (assigned instanceof GenericArrayType) {
             GenericArrayType p = (GenericArrayType) assigned;
             if (assignee instanceof Class<?>) {
                 Class<?> m = (Class<?>) assignee;
                 if (!m.isArray()) {
                     return false;
                 }
                 Type pComponent = p.getGenericComponentType();
                 Type mComponent = m.getComponentType();
                 return matches(pComponent, mComponent);
             }
             if (assignee instanceof ParameterizedType) {
                 return false;
             }
             if (assignee instanceof TypeVariable<?>) {
                 return false;
             }
             if (assignee instanceof WildcardType) {
                 return matchesTypeWildcard(assigned, (WildcardType) assignee);
             }
             if (assignee instanceof GenericArrayType) {
                 GenericArrayType m = (GenericArrayType) assignee;
                 Type pComponent = p.getGenericComponentType();
                 Type mComponent = m.getGenericComponentType();
                 return matches(pComponent, mComponent);
             }
             return false;
         }
         return false;
    }

    private static boolean isAssignableTypeArgs(Type[] pArgs, Type[] mArgs) {
        int size = Math.max(pArgs.length, mArgs.length);
        for (int i = 0; i < size; i++) {
            Type p = i < pArgs.length ? pArgs[i] : JieType.questionMark();
            Type m = i < mArgs.length ? mArgs[i] : JieType.questionMark();
            if (!isAssignable(p, m)) {
                return false;
            }
        }
        return true;
    }
}
