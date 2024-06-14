package xyz.fslabo.common.reflect;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.GekString;
import xyz.fslabo.common.collect.JieArray;
import xyz.fslabo.common.collect.JieColl;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Utilities for types.
 *
 * @author fredsuvn
 */
public class GekType {

    /**
     * Returns parameterized type with given raw type, owner type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param ownerType           given owner type
     * @param actualTypeArguments actual type arguments
     * @return parameterized type
     */
    public static GekParamType paramType(Type rawType, @Nullable Type ownerType, Iterable<Type> actualTypeArguments) {
        return paramType(rawType, ownerType, JieColl.toArray(actualTypeArguments, Type.class));
    }

    /**
     * Returns parameterized type with given raw type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param actualTypeArguments actual type arguments
     * @return parameterized type
     */
    public static GekParamType paramType(Type rawType, Iterable<Type> actualTypeArguments) {
        return paramType(rawType, null, actualTypeArguments);
    }

    /**
     * Returns parameterized type with given raw type, owner type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param ownerType           given owner type
     * @param actualTypeArguments actual type arguments
     * @return parameterized type
     */
    public static GekParamType paramType(Type rawType, @Nullable Type ownerType, Type[] actualTypeArguments) {
        return new GekParamTypeImpl(rawType, ownerType, actualTypeArguments);
    }

    /**
     * Returns parameterized type with given raw type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param actualTypeArguments actual type arguments
     * @return parameterized type
     */
    public static GekParamType paramType(Type rawType, Type[] actualTypeArguments) {
        return paramType(rawType, null, actualTypeArguments);
    }

    /**
     * Returns given parameterized type as {@link GekParamType}.
     *
     * @param type given parameterized type
     * @return parameterized type
     */
    public static GekParamType paramType(ParameterizedType type) {
        return paramType(type.getRawType(), type.getOwnerType(), type.getActualTypeArguments());
    }

    /**
     * Returns wildcard type with given upper bounds and lower bounds.
     *
     * @param upperBounds given upper bounds
     * @param lowerBounds given lower bounds
     * @return wildcard type
     */
    public static GekWildcard wildcard(@Nullable Iterable<Type> upperBounds, @Nullable Iterable<Type> lowerBounds) {
        return new GekWildcardImpl(
            upperBounds == null ? null : JieColl.toArray(upperBounds, Type.class),
            lowerBounds == null ? null : JieColl.toArray(lowerBounds, Type.class)
        );
    }

    /**
     * Returns wildcard type with given upper bounds and lower bounds.
     *
     * @param upperBounds given upper bounds
     * @param lowerBounds given lower bounds
     * @return wildcard type
     */
    public static GekWildcard wildcard(@Nullable Type[] upperBounds, @Nullable Type[] lowerBounds) {
        return new GekWildcardImpl(upperBounds, lowerBounds);
    }

    /**
     * Returns given wildcard type as {@link GekWildcard}.
     *
     * @param type given wildcard type
     * @return wildcard type
     */
    public static GekWildcard wildcard(WildcardType type) {
        return new GekWildcardImpl(type.getUpperBounds(), type.getLowerBounds());
    }

    /**
     * Returns generic array type with given component type.
     *
     * @param componentType given component type
     * @return generic array type
     */
    public static GekArrayType arrayType(Type componentType) {
        return new GekArrayTypeImpl(componentType);
    }

    /**
     * Returns given generic array type as {@link GekArrayType}.
     *
     * @param type given generic array type
     * @return generic array type
     */
    public static GekArrayType arrayType(GenericArrayType type) {
        return new GekArrayTypeImpl(type.getGenericComponentType());
    }

    private static final class GekParamTypeImpl implements GekParamType {

        private final Type rawType;
        private final @Nullable Type ownerType;
        private final Type[] actualTypeArguments;
        private List<Type> actualTypeArgumentList;

        private GekParamTypeImpl(Type rawType, @Nullable Type ownerType, Type[] actualTypeArguments) {
            this.rawType = rawType;
            this.ownerType = ownerType == null ?
                ((rawType instanceof Class) ? ((Class<?>) rawType).getDeclaringClass() : null) : ownerType;
            this.actualTypeArguments = actualTypeArguments.clone();
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof GekParamTypeImpl) {
                GekParamTypeImpl other = (GekParamTypeImpl) o;
                return Objects.equals(rawType, other.rawType)
                    && Objects.equals(ownerType, other.ownerType)
                    && Arrays.equals(actualTypeArguments, other.actualTypeArguments);
            }
            if (o instanceof ParameterizedType) {
                ParameterizedType other = (ParameterizedType) o;
                return Objects.equals(rawType, other.getRawType())
                    && Objects.equals(ownerType, other.getOwnerType())
                    && Arrays.equals(actualTypeArguments, other.getActualTypeArguments());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.actualTypeArguments)
                ^ (this.ownerType == null ? 0 : this.ownerType.hashCode())
                ^ (this.rawType == null ? 0 : this.rawType.hashCode());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (ownerType != null) {
                //test.A<String>
                sb.append(ownerType.getTypeName());
                //test.A$B
                String rawTypeName = rawType.getTypeName();
                //test.A
                String ownerRawTypeName;
                if (ownerType instanceof ParameterizedType) {
                    ownerRawTypeName = ((ParameterizedType) ownerType).getRawType().getTypeName();
                } else {
                    ownerRawTypeName = ownerType.getTypeName();
                }
                //test.A$B -> $B
                String lastName = GekString.replace(rawTypeName, ownerRawTypeName, "");
                //test.A<String>$B
                sb.append(lastName);
            } else {
                sb.append(rawType.getTypeName());
            }
            if (JieArray.isNotEmpty(actualTypeArguments)) {
                sb.append("<");
                boolean first = true;
                for (Type t : actualTypeArguments) {
                    if (!first) {
                        sb.append(", ");
                    }
                    if (t instanceof Class) {
                        sb.append(((Class<?>) t).getName());
                    } else {
                        sb.append(t.toString());
                    }
                    first = false;
                }
                sb.append(">");
            }
            return sb.toString();
        }

        @Override
        public List<Type> getActualTypeArgumentList() {
            if (actualTypeArgumentList == null) {
                actualTypeArgumentList = JieColl.asList(actualTypeArguments);
            }
            return actualTypeArgumentList;
        }
    }

    private static final class GekWildcardImpl implements GekWildcard {

        private final Type[] upperBounds;
        private List<Type> upperBoundList;
        private final Type[] lowerBounds;
        private List<Type> lowerBoundList;

        private GekWildcardImpl(@Nullable Type[] upperBounds, @Nullable Type[] lowerBounds) {
            this.upperBounds = JieArray.isEmpty(upperBounds) ? new Type[]{Object.class} : upperBounds.clone();
            this.lowerBounds = JieArray.isEmpty(lowerBounds) ? new Type[0] : lowerBounds.clone();
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds.clone();
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds.clone();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof GekWildcardImpl) {
                GekWildcardImpl other = (GekWildcardImpl) o;
                return Arrays.equals(upperBounds, other.upperBounds)
                    && Arrays.equals(lowerBounds, other.lowerBounds);
            }
            if (o instanceof WildcardType) {
                WildcardType other = (WildcardType) o;
                return Arrays.equals(upperBounds, other.getUpperBounds())
                    && Arrays.equals(lowerBounds, other.getLowerBounds());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(upperBounds) ^ Arrays.hashCode(lowerBounds);
        }

        @Override
        public String toString() {
            StringBuilder builder;
            Type[] bounds;
            if (lowerBounds.length == 0) {
                if (upperBounds.length == 0 || Objects.equals(Object.class, upperBounds[0])) {
                    return "?";
                }

                bounds = upperBounds;
                builder = new StringBuilder("? extends ");
            } else {
                bounds = lowerBounds;
                builder = new StringBuilder("? super ");
            }
            for (int i = 0; i < bounds.length; ++i) {
                if (i > 0) {
                    builder.append(" & ");
                }

                builder.append(bounds[i] instanceof Class ? ((Class<?>) bounds[i]).getName() : bounds[i].toString());
            }
            return builder.toString();
        }

        @Override
        public List<Type> getUpperBoundList() {
            if (upperBoundList == null) {
                upperBoundList = JieColl.asList(upperBounds);
            }
            return upperBoundList;
        }

        @Override
        public List<Type> getLowerBoundList() {
            if (lowerBoundList == null) {
                lowerBoundList = JieColl.asList(lowerBounds);
            }
            return lowerBoundList;
        }
    }

    private static final class GekArrayTypeImpl implements GekArrayType {

        private final Type componentType;

        private GekArrayTypeImpl(Type componentType) {
            this.componentType = componentType;
        }

        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof GenericArrayType) {
                GenericArrayType other = (GenericArrayType) o;
                return Objects.equals(componentType, other.getGenericComponentType());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(componentType);
        }

        @Override
        public String toString() {
            Type type = this.getGenericComponentType();
            StringBuilder builder = new StringBuilder();
            if (type instanceof Class) {
                builder.append(((Class<?>) type).getName());
            } else {
                builder.append(type.toString());
            }
            builder.append("[]");
            return builder.toString();
        }
    }
}
