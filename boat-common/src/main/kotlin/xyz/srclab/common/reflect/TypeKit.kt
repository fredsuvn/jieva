@file:JvmName("TypeKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.annotation.PossibleTypes
import xyz.srclab.common.base.asAny
import java.lang.reflect.*

val Type.rawClass: Class<*>
    get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            else -> throw IllegalArgumentException("$this has no raw type.")
        }
    }

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
            is TypeVariable<*> -> Nothing::class.java
            is WildcardType -> this.lowerBound
            else -> this
        }
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val Type.deepUpperBound: Type
    get() {
        return when (this) {
            is ParameterizedType -> {
                val actualTypeArguments = this.actualTypeArguments
                var needTransform = false
                for (i in actualTypeArguments.indices) {
                    val oldType = actualTypeArguments[i]
                    val newType = oldType.deepUpperBound
                    if (oldType !== newType) {
                        needTransform = true
                        actualTypeArguments[i] = newType
                    }
                }
                if (!needTransform) {
                    return this
                }
                return ParameterizedTypeImpl(this.rawType, this.ownerType, actualTypeArguments)
            }
            is TypeVariable<*> -> this.upperBound
            is WildcardType -> this.upperBound
            else -> this
        }
    }

@get:PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
val Type.deepLowerBound: Type
    get() {
        return when (this) {
            is ParameterizedType -> {
                val actualTypeArguments = this.actualTypeArguments
                var needTransform = false
                for (i in actualTypeArguments.indices) {
                    val oldType = actualTypeArguments[i]
                    val newType = oldType.deepLowerBound
                    if (oldType !== newType) {
                        needTransform = true
                        actualTypeArguments[i] = newType
                    }
                }
                if (!needTransform) {
                    return this
                }
                return parameterizedType(this.rawType, this.ownerType, actualTypeArguments)
            }
            is TypeVariable<*> -> this.lowerBound
            is WildcardType -> this.lowerBound
            else -> this
        }
    }

fun parameterizedType(rawType: Type, ownerType: Type?, actualTypeArguments: Array<Type>?): ParameterizedType {
    return ParameterizedTypeImpl(rawType, ownerType, actualTypeArguments)
}

private class ParameterizedTypeImpl(
    private val rawType: Type,
    private val ownerType: Type?,
    actualTypeArguments: Array<Type>?,
) : ParameterizedType {

    private val actualTypeArguments: Array<Type>? = getActualTypeArguments0(actualTypeArguments)

    override fun getRawType(): Type {
        return rawType
    }

    override fun getOwnerType(): Type? {
        return ownerType
    }

    override fun getActualTypeArguments(): Array<Type> {
        return getActualTypeArguments0(actualTypeArguments)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParameterizedTypeImpl

        if (rawType != other.rawType) return false
        if (ownerType != other.ownerType) return false
        if (!actualTypeArguments.contentEquals(other.actualTypeArguments)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawType.hashCode()
        result = 31 * result + ownerType.hashCode()
        result = 31 * result + actualTypeArguments.contentHashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (ownerType !== null) {
            if (ownerType is Class<*>) {
                sb.append(ownerType.name)
            } else {
                sb.append(ownerType.toString())
            }
            sb.append(".")
            sb.append(rawType.typeName)
        } else {
            sb.append(rawType.typeName)
        }

        if (actualTypeArguments !== null && actualTypeArguments.isNotEmpty()) {
            sb.append("<")
            var first = true
            val var3 = actualTypeArguments
            val var4 = var3.size
            for (var5 in 0 until var4) {
                val t = var3[var5]
                if (!first) {
                    sb.append(", ")
                }
                if (t is Class<*>) {
                    sb.append(t.name)
                } else {
                    sb.append(t.toString())
                }
                first = false
            }
            sb.append(">")
        }

        return sb.toString()
    }

    fun getActualTypeArguments0(actualTypeArguments: Array<Type>?): Array<Type> {
        return if (actualTypeArguments === null) ArrayUtils.EMPTY_TYPE_ARRAY else actualTypeArguments.clone()
    }
}

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.genericSignature(target: Class<*>): Type {
    return this.findGenericSignature(target)
        ?: throw IllegalArgumentException("Generic signature of $target for $this was not found.")
}

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.findGenericSignature(vararg targets: Class<*>): Type? {
    for (target in targets) {
        val result = if (target.isInterface)
            this.findGenericInterface(target)
        else
            this.findGenericSuperclass(target)
        if (result !== null) {
            return result
        }
    }
    return null
}

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.findGenericSuperclass(vararg targets: Class<*>): Type? {
    var rawClass = this.upperClass
    if (targets.contains(rawClass)) {
        return this
    }

    var genericType: Type? = rawClass.genericSuperclass
    while (genericType !== null) {
        rawClass = genericType.upperClass
        if (targets.contains(rawClass)) {
            return genericType
        }
        genericType = rawClass.genericSuperclass
    }
    return null
}

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.findGenericInterface(vararg targets: Class<*>): Type? {
    val rawClass = this.upperClass
    if (targets.contains(rawClass)) {
        return this
    }

    val genericInterfaces = rawClass.genericInterfaces

    fun findInterface(genericTypes: Array<out Type>, vararg targets: Class<*>): Type? {
        //Search level first
        for (genericType in genericTypes) {
            if (targets.contains(genericType.upperClass)) {
                return genericType
            }
        }
        for (genericType in genericTypes) {
            val result = findInterface(genericType.upperClass.genericInterfaces, *targets)
            if (result !== null) {
                return result
            }
        }
        return null
    }

    return findInterface(genericInterfaces, *targets)
}

/**
 * <T>(StringFoo.class) -> String
 */
fun TypeVariable<*>.actualType(lowerType: Type): Type {
    return this.findActualType(lowerType)
        ?: throw IllegalArgumentException("Actual type of $this on $lowerType was not found.")
}

/**
 * <T>(StringFoo.class) -> String
 */
fun TypeVariable<*>.findActualType(lowerType: Type): Type? {
    val declaredType = this.genericDeclaration
    if (declaredType !is Class<*>) {
        return null
    }

    val genericSignature = lowerType.findGenericSignature(declaredType)
    if (genericSignature === null || genericSignature !is ParameterizedType) {
        return null
    }

    val typeParameters = declaredType.typeParameters
    val actualTypeArguments = genericSignature.actualTypeArguments
    val index = typeParameters.indexOf(this)
    if (index < 0) {
        return null
    }
    return actualTypeArguments[index]
}