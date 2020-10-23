@file:JvmName("TypeKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.annotation.PossibleTypes
import java.lang.reflect.*

val Type.rawClass: Class<*>
    get() {
        if (this is Class<*>) {
            return this
        }
        if (this is ParameterizedType) {
            return this.rawType.rawClass
        }
        if (this is TypeVariable<*> && this.bounds.isNotEmpty()) {
            val boundType = this.bounds[0]
            return boundType.rawClass
        }
        if (this is WildcardType && this.upperBounds.isNotEmpty()) {
            val upperBound = this.upperBounds[0]
            return upperBound.rawClass
        }
        return Any::class.java
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val TypeVariable<*>.upperBound: Type
    get() {
        return this.bounds[0].upperBound
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val WildcardType.upperBound: Type
    get() {
        return if (this.upperBounds.isNotEmpty()) {
            this.upperBounds[0].upperBound
        } else Any::class.java
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val Type.upperBound: Type
    get() {
        return when (this) {
            is TypeVariable<*> -> upperBound
            is WildcardType -> upperBound
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

        var rawClass = type.rawClass
        if (rawClass == target) {
            return type
        }

        if (!target.isAssignableFrom(rawClass)) {
            throw IllegalArgumentException("$target is not super class of $type")
        }

        var genericType: Type? = rawClass.genericSuperclass
        while (genericType !== null) {
            rawClass = genericType.rawClass
            if (rawClass == target) {
                return genericType
            }
            genericType = rawClass.genericSuperclass
        }
        throw IllegalArgumentException("Cannot find generic super class: type = $type, target = $target")
    }

    @PossibleTypes(Class::class, ParameterizedType::class)
    fun findInterface(type: Type, target: Class<*>): Type {

        val rawClass = type.rawClass
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
                if (genericType.rawClass == target) {
                    return genericType
                }
            }
            for (genericType in genericTypes) {
                val result = findInterface(genericType.rawClass.genericInterfaces, target)
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
        val genericTargetType = target.genericTypeFor(declaredClass)
        if (genericTargetType !is ParameterizedType) {
            throw IllegalArgumentException(
                "Cannot find actual type: type = $type, declaredClass: $declaredClass, target = $target"
            )
        }
        val actualArguments = genericTargetType.actualTypeArguments
        if (actualArguments.size != typeParameters.size) {
            throw IllegalArgumentException(
                "Cannot find actual type: type = $type, declaredClass: $declaredClass, target = $target"
            )
        }
        return actualArguments[index]
    }
}