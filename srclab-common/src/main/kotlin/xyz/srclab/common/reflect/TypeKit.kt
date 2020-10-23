@file:JvmName("TypeKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.annotation.PossibleTypes
import xyz.srclab.common.base.asAny
import java.lang.reflect.*

val ParameterizedType.rawClass: Class<*>
    get() {
        return this.rawType.asAny()
    }

val Type.upperClass: Class<*>
    get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is TypeVariable<*> -> {
                val bounds = this.bounds
                return if (!bounds.isNullOrEmpty()) {
                    bounds[0].upperClass
                } else {
                    Any::class.java
                }
            }
            is WildcardType -> {
                val upperBounds = this.upperBounds
                return if (!upperBounds.isNullOrEmpty()) {
                    upperBounds[0].upperClass
                } else {
                    Any::class.java
                }
            }
            else -> Any::class.java
        }
    }

val Type.lowerClass: Class<*>
    get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is WildcardType -> {
                val lowerBounds = this.lowerBounds
                return if (!lowerBounds.isNullOrEmpty()) {
                    lowerBounds[0].lowerClass
                } else {
                    Nothing::class.java
                }
            }
            else -> Nothing::class.java
        }
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val TypeVariable<*>.upperBound: Type
    get() {
        val bounds = this.bounds
        return if (!bounds.isNullOrEmpty()) {
            bounds[0].upperBound
        } else {
            Any::class.java
        }
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val WildcardType.upperBound: Type
    get() {
        val upperBounds = this.upperBounds
        return if (!upperBounds.isNullOrEmpty()) {
            upperBounds[0].upperBound
        } else {
            Any::class.java
        }
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val Type.upperBound: Type
    get() {
        return when (this) {
            is TypeVariable<*> -> this.upperBound
            is WildcardType -> this.upperBound
            else -> this
        }
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val WildcardType.lowerBound: Type
    get() {
        val lowerBounds = this.lowerBounds
        return if (!lowerBounds.isNullOrEmpty()) {
            lowerBounds[0].lowerBound
        } else {
            Nothing::class.java
        }
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val Type.lowerBound: Type
    get() {
        return when (this) {
            is WildcardType -> this.lowerBound
            else -> this
        }
    }

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.genericTypeFor(target: Class<*>): Type {
    return if (target.isInterface) this.genericInterfaceFor(target) else this.genericSuperClassFor(target)
}

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.genericSuperClassFor(targetSuperclass: Class<*>): Type {
    return GenericTypeFinder.findSuperclass(this, targetSuperclass)
}

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.genericInterfaceFor(targetInterface: Class<*>): Type {
    return GenericTypeFinder.findInterface(this, targetInterface)
}

/**
 * <T>(Foo.class, StringFoo.class) -> String
 */
fun TypeVariable<*>.actualTypeFor(declaredClass: Class<*>, target: Type): Type {
    return ActualTypeFinder.findActualType(this, declaredClass, target)
}

private object GenericTypeFinder {

    @PossibleTypes(Class::class, ParameterizedType::class)
    fun findSuperclass(type: Type, target: Class<*>): Type {

        var rawClass = type.upperClass
        if (rawClass == target) {
            return type
        }

        if (!target.isAssignableFrom(rawClass)) {
            throw IllegalArgumentException("$target is not super class of $type")
        }

        var genericType: Type? = rawClass.genericSuperclass
        while (genericType !== null) {
            rawClass = genericType.upperClass
            if (rawClass == target) {
                return genericType
            }
            genericType = rawClass.genericSuperclass
        }
        throw IllegalArgumentException("Cannot find generic super class: type = $type, target = $target")
    }

    @PossibleTypes(Class::class, ParameterizedType::class)
    fun findInterface(type: Type, target: Class<*>): Type {

        val rawClass = type.upperClass
        if (rawClass == target) {
            return type
        }

        if (!target.isAssignableFrom(rawClass)) {
            throw IllegalArgumentException("$target is not interface of $type")
        }

        val genericInterfaces = rawClass.genericInterfaces

        fun findInterface(genericTypes: Array<out Type>, target: Class<*>): Type? {
            //Search level first
            for (genericType in genericTypes) {
                if (genericType.upperClass == target) {
                    return genericType
                }
            }
            for (genericType in genericTypes) {
                val result = findInterface(genericType.upperClass.genericInterfaces, target)
                if (result !== null) {
                    return result
                }
            }
            return null
        }

        val result = findInterface(genericInterfaces, target)
        if (result !== null) {
            return result
        }
        throw IllegalArgumentException("Cannot find generic interface: type = $type, target = $target")
    }
}

private object ActualTypeFinder {

    fun findActualType(type: TypeVariable<*>, declaredClass: Class<*>, target: Type): Type {
        val typeParameters = declaredClass.typeParameters
        val index = typeParameters.indexOf(type)
        if (index < 0) {
            throw IllegalArgumentException(
                "Cannot find type variable: type = $type, declaredClass: $declaredClass, target = $target"
            )
        }
        val actualArguments =
            when (val genericTargetType = target.genericTypeFor(declaredClass)) {
                is Class<*> -> genericTargetType.typeParameters
                is ParameterizedType -> genericTargetType.actualTypeArguments
                else -> throw IllegalArgumentException(
                    "Cannot find actual type: type = $type, declaredClass: $declaredClass, target = $target"
                )
            }
        if (actualArguments.size != typeParameters.size) {
            throw IllegalArgumentException(
                "Cannot find actual type: type = $type, declaredClass: $declaredClass, target = $target"
            )
        }
        return actualArguments[index]
    }
}