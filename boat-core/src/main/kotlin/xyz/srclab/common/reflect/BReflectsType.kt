@file:JvmName("BReflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.annotations.Accepted
import xyz.srclab.annotations.Written
import xyz.srclab.common.base.asTyped
import java.lang.reflect.*

val Type.isArray: Boolean
    get() {
        return when (this) {
            is Class<*> -> this.isArray
            is GenericArrayType -> true
            else -> false
        }
    }

val Type.componentTypeOrNull: Type?
    get() {
        return when (this) {
            is Class<*> -> this.componentType
            is GenericArrayType -> this.genericComponentType
            else -> null
        }
    }

val ParameterizedType.rawClass: Class<*>
    get() {
        return this.rawType.asTyped()
    }

val GenericArrayType.rawClass: Class<*>
    get() {
        var deep = 0
        var componentType = this.genericComponentType
        while (componentType is GenericArrayType) {
            deep++
            componentType = componentType.genericComponentType
        }
        var arrayClass = componentType.rawClass.arrayClass
        for (i in 0 until deep) {
            arrayClass = arrayClass.arrayClass
        }
        return arrayClass
    }

val Type.rawClass: Class<*>
    @Accepted(Class::class, ParameterizedType::class, GenericArrayType::class)
    @Throws(IllegalArgumentException::class)
    get() {
        return this.rawClassOrNull ?: throw IllegalArgumentException(
            "Only Class, ParameterizedType or GenericArrayType has raw class: $this"
        )
    }

val Type.rawClassOrNull: Class<*>?
    get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is GenericArrayType -> this.rawClass
            else -> null
        }
    }

val TypeVariable<*>.upperBound: Type
    get() {
        val bounds = this.bounds
        return if (bounds.isEmpty()) {
            Any::class.java
        } else {
            bounds[0].upperBound
        }
    }

val WildcardType.upperBound: Type
    get() {
        val upperBounds = this.upperBounds
        return if (upperBounds.isEmpty()) {
            Any::class.java
        } else {
            upperBounds[0].upperBound
        }
    }

val Type.upperBound: Type
    get() {
        return when (this) {
            is TypeVariable<*> -> this.upperBound
            is WildcardType -> this.upperBound
            else -> this
        }
    }

val WildcardType.lowerBound: Type?
    get() {
        val lowerBounds = this.lowerBounds
        return if (lowerBounds.isEmpty()) {
            null
        } else {
            lowerBounds[0].lowerBound
        }
    }

val Type.lowerBound: Type?
    get() {
        return when (this) {
            is WildcardType -> this.lowerBound
            is TypeVariable<*> -> null
            else -> this
        }
    }

/**
 * Returns type arguments.
 *
 * Assume `class Foo<T>` and its subclass `class StringFoo extends Foo<String>`:
 *
 * ```
 * Reflects.typeArguments(StringFoo.class, Foo.class);
 * ```
 *
 * will return: `{T=java.lang.String}`
 */
@Throws(IllegalArgumentException::class)
fun @receiver:Accepted(
    Class::class,
    ParameterizedType::class,
    GenericArrayType::class
) Type.getTypeArguments(): Map<TypeVariable<*>, Type> {

    fun Type.resolveTypeParameters(to: MutableMap<TypeVariable<*>, Type>) {

        fun ParameterizedType.resolveTypeArguments(to: MutableMap<TypeVariable<*>, Type>) {
            val actualArguments = this.actualTypeArguments
            val typeParameters = this.rawClass.typeParameters
            for (i in typeParameters.indices) {
                to[typeParameters[i]] = actualArguments[i]
            }
        }

        fun Class<*>.resolveTypeParameters(to: MutableMap<TypeVariable<*>, Type>) {

            fun Class<*>.doInterfaces() {
                val genericInterfaces = this.genericInterfaces
                for (genericInterface in genericInterfaces) {
                    if (genericInterface is ParameterizedType) {
                        genericInterface.resolveTypeArguments(to)
                    }
                }
                for (genericInterface in genericInterfaces) {
                    genericInterface.rawClass.doInterfaces()
                }
            }

            fun Class<*>.doSuperclass() {
                val genericSuperclass = this.genericSuperclass
                if (genericSuperclass is ParameterizedType) {
                    genericSuperclass.resolveTypeArguments(to)
                }
                this.doInterfaces()
                if (genericSuperclass !== null) {
                    genericSuperclass.rawClass.doSuperclass()
                }
            }

            doSuperclass()
        }

        when (this) {
            is Class<*> -> {
                this.resolveTypeParameters(to)
            }
            is ParameterizedType -> {
                val ownerType = this.ownerType
                if (ownerType !== null) {
                    ownerType.resolveTypeParameters(to)
                }
                this.resolveTypeArguments(to)
                this.rawClass.resolveTypeParameters(to)
            }
            is GenericArrayType -> {
                this.genericComponentType.resolveTypeParameters(to)
            }
            else -> throw IllegalArgumentException("Cannot resolve type arguments for $this")
        }
    }

    val typeArguments: MutableMap<TypeVariable<*>, Type> = HashMap()
    this.resolveTypeParameters(typeArguments)
    val stack: MutableSet<TypeVariable<*>> = HashSet()
    for (entry in typeArguments) {
        stack.clear()
        val oldType = entry.value
        val newType = oldType.eraseTypeParameters(typeArguments, stack, true)
        if (newType != oldType) {
            entry.setValue(newType)
        }
    }
    return typeArguments
}

/**
 * Returns a [ParameterizedType] as signature of [this], target for [to] type.
 *
 * Assume `class Foo<T>` and its subclass `class StringFoo` extends `Foo<String>`:
 *
 * ```
 * Reflects.toTypeSignature(StringFoo.class, Foo.class);
 * ```
 *
 * will return: `Foo<String>`
 */
@Throws(IllegalArgumentException::class)
fun Type.getTypeSignature(to: Class<*>): ParameterizedType {
    val typeParameters = to.typeParameters
    val typeArguments = this.getTypeArguments()
    val actualArguments = typeParameters.map {
        typeArguments[it] ?: it
    }
    val owner = to.declaringClass
    return if (owner === null) {
        parameterizedType(to, actualArguments)
    } else {
        parameterizedTypeWithOwner(to, owner, actualArguments)
    }
}

/**
 * Erase type parameter if [this] contains [TypeVariable].
 * Return itself if there is no argument found in [typeArguments].
 */
@JvmOverloads
fun Type.eraseTypeParameters(
    typeArguments: Map<TypeVariable<*>, Type>,
    @Written searchStack: MutableSet<TypeVariable<*>> = HashSet()
): Type {
    return eraseTypeParameters(typeArguments.asTyped(), searchStack, false)
}

private fun Type.eraseTypeParameters(
    @Written typeArguments: MutableMap<TypeVariable<*>, Type>,
    @Written searchStack: MutableSet<TypeVariable<*>>,
    replaceTypeParameter: Boolean,
): Type {

    fun getAndSwapTypes(@Written array: Array<Type>): Boolean {
        var changed = false
        for (i in array.indices) {
            val oldType = array[i]
            val newType = oldType.eraseTypeParameters(typeArguments, searchStack)
            if (newType != oldType) {
                array[i] = newType
                changed = true
            }
        }
        return changed
    }

    when (this) {
        is ParameterizedType -> {
            val actualTypeArguments = this.actualTypeArguments
            return if (getAndSwapTypes(actualTypeArguments)) {
                this.withArguments(*actualTypeArguments)
            } else {
                this
            }
        }
        is WildcardType -> {
            val upperBounds = this.upperBounds
            val lowerBounds = this.lowerBounds
            return if (getAndSwapTypes(upperBounds) || getAndSwapTypes(lowerBounds)) {
                this.withBounds(upperBounds, lowerBounds)
            } else {
                this
            }
        }
        is GenericArrayType -> {
            val componentType = this.genericComponentType
            val newComponentType = componentType.eraseTypeParameters(typeArguments, searchStack)
            return if (newComponentType != componentType) {
                newComponentType.genericArrayType()
            } else {
                this
            }
        }
        is TypeVariable<*> -> {
            val value = typeArguments[this]
            if (value === null || searchStack.contains(value)) {
                return this
            }
            if (value !is TypeVariable<*>) {
                val newType = value.eraseTypeParameters(typeArguments, searchStack)
                if (replaceTypeParameter && newType != this) {
                    for (typeVariable in searchStack) {
                        typeArguments[typeVariable] = newType
                    }
                }
                return newType
            }
            searchStack.add(this)
            return value.eraseTypeParameters(typeArguments, searchStack)
        }
        else -> return this
    }
}