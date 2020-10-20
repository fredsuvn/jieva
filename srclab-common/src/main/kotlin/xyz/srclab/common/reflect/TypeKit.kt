package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.annotation.Nullable
import xyz.srclab.annotation.ReturnType
import xyz.srclab.common.base.Format
import xyz.srclab.common.cache.Cache
import java.lang.IllegalArgumentException
import java.lang.reflect.*
import java.util.function.Function

object TypeKit {
    @JvmStatic
    fun getRawType(type: Type?): Class<*> {
        if (type is Class<*>) {
            return type
        }
        if (type is ParameterizedType) {
            return getRawType(type.rawType)
        }
        if (type is TypeVariable<*>) {
            val boundType = type.bounds[0]
            return if (boundType is Class<*>) {
                boundType
            } else getRawType(boundType)
        }
        if (type is WildcardType) {
            val upperBounds = type.upperBounds
            if (upperBounds.size == 1) {
                return getRawType(upperBounds[0])
            }
        }
        return Any::class.java
    }

    @ReturnType(Class::class, ParameterizedType::class)
    fun getGenericSignature(type: Type, target: Class<*>): Type {
        return if (target.isInterface) getGenericInterface(type, target) else getGenericSuperclass(type, target)!!
    }

    @ReturnType(Class::class, ParameterizedType::class)
    fun getGenericSuperclass(type: Type, targetSuperclass: Class<*>): Type? {
        return GenericSignatureFinder.findSuperclass(type, targetSuperclass)
    }

    @JvmStatic
    @ReturnType(Class::class, ParameterizedType::class)
    fun getGenericInterface(type: Type, targetInterface: Class<*>): Type {
        return GenericSignatureFinder.findInterface(type, targetInterface)
    }

    fun tryActualType(type: Type, owner: Type, declaringClass: Class<*>): Type {
        return if (type is TypeVariable<*>) {
            tryActualType(type, owner, declaringClass)
        } else type
    }

    fun tryActualType(
        typeVariable: TypeVariable<*>?, owner: Type, declaringClass: Class<*>
    ): Type {
        return ActualTypeFinder.find(typeVariable, owner, declaringClass)
    }

    @ReturnType(Class::class, ParameterizedType::class, GenericArrayType::class)
    fun getUpperBound(type: TypeVariable<*>): Type {
        return getUpperBound(type.bounds[0])
    }

    @ReturnType(Class::class, ParameterizedType::class, GenericArrayType::class)
    fun getUpperBound(type: WildcardType): Type {
        val upperBounds = type.upperBounds
        return if (ArrayUtils.isNotEmpty(upperBounds)) {
            getUpperBound(upperBounds[0])
        } else Any::class.java
    }

    @JvmStatic
    @ReturnType(Class::class, ParameterizedType::class, GenericArrayType::class)
    fun getUpperBound(type: Type): Type {
        if (type is TypeVariable<*>) {
            return getUpperBound(type)
        }
        return if (type is WildcardType) {
            getUpperBound(type)
        } else type
    }

    private object GenericSignatureFinder {
        private val cache: Cache<Key, Type> = Cache.commonCache<Key, Type>()
        @ReturnType(Class::class, ParameterizedType::class)
        fun findSuperclass(type: Type, targetSuperclass: Class<*>): Type? {
            @Nullable var currentType = getUpperBound(type)
            do {
                val currentClass = getRawType(currentType)
                if (targetSuperclass == currentClass) {
                    return currentType
                }
                currentType = currentClass.genericSuperclass
            } while (currentType != null)
            throw IllegalArgumentException(
                "Cannot find generic super class: type = $type, target = $targetSuperclass"
            )
        }

        @ReturnType(Class::class, ParameterizedType::class)
        fun findInterface(type: Type, targetInterface: Class<*>): Type {
            val rawType = getRawType(getUpperBound(type))
            return if (rawType == targetInterface) {
                type
            } else cache.getNonNull(
                Key.keyOf(rawType, targetInterface),
                Function<Key, Type> { k: Key? -> findFromRawType(rawType, targetInterface) })
        }

        private fun findFromRawType(rawType: Class<*>, targetInterface: Class<*>): Type {
            val genericInterfaces = rawType.genericInterfaces
            if (!ArrayUtils.isEmpty(genericInterfaces)) {
                for (type in genericInterfaces) {
                    @Nullable val type0 = find0(type, targetInterface)
                    if (type0 != null) {
                        return type0
                    }
                }
            }
            throw IllegalArgumentException(
                "Cannot find generic super interface: type = $rawType, target = $targetInterface"
            )
        }

        @Nullable
        private fun find0(type: Type, targetInterface: Class<*>): Type? {
            val rawType = getRawType(getUpperBound(type))
            if (rawType == targetInterface) {
                return type
            }
            val types = rawType.genericInterfaces
            if (ArrayUtils.isEmpty(types)) {
                return null
            }
            for (t in types) {
                @Nullable val type0 = find0(t, targetInterface)
                if (type0 != null) {
                    return type0
                }
            }
            return null
        }
    }

    private object ActualTypeFinder {
        private val cache: Cache<Key, Type> = Cache.commonCache<Key, Type>()
        fun find(type: TypeVariable<*>?, owner: Type, declaringClass: Class<*>): Type {
            return cache.getNonNull(
                Key.keyOf(type, owner, declaringClass),
                Function<Key, Type?> { k: Key? -> findTypeVariable(type, owner, declaringClass) }
            )
        }

        private fun findTypeVariable(type: TypeVariable<*>?, owner: Type, declaringClass: Class<*>): Type? {
            val genericSignature = getGenericSignature(owner, declaringClass) as? ParameterizedType ?: return type
            val actualTypeArguments = genericSignature.actualTypeArguments
            return findTypeVariable0(type, actualTypeArguments, declaringClass)
                ?: throw IllegalArgumentException(
                    Format.fast(
                        "Cannot find actual type {} from {} to {}",
                        type, declaringClass, owner
                    )
                )
        }

        @Nullable
        private fun findTypeVariable0(
            type: TypeVariable<*>?, actualTypeArguments: Array<Type>, declaringClass: Class<*>
        ): Type? {
            val typeVariables: Array<TypeVariable<*>> = declaringClass.getTypeParameters()
            val index = ArrayUtils.indexOf(typeVariables, type)
            return if (index < 0) {
                null
            } else actualTypeArguments[index]
        }
    }
}