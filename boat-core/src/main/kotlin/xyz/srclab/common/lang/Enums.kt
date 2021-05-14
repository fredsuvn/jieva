@file:JvmName("Enums")

package xyz.srclab.common.lang

@Throws(IllegalArgumentException::class)
@JvmName("value")
fun <T : Enum<T>> Class<*>.valueOfEnum(name: CharSequence): T {
    val result = valueOfEnumOrNull<T>(name)
    if (result !== null) {
        return result
    }
    throw IllegalArgumentException("$name not found in $this")
}

@JvmName("valueOrNull")
fun <T : Enum<T>> Class<*>.valueOfEnumOrNull(name: CharSequence): T? {
    return JavaEnums.valueOf(this.asAny<Class<T>>(), name.toString()).asAny()
}

@Throws(IllegalArgumentException::class)
@JvmName("valueIgnoreCase")
fun <T : Enum<T>> Class<*>.valueOfEnumIgnoreCase(name: CharSequence): T {
    val result = valueOfEnumIgnoreCaseOrNull<T>(name)
    if (result !== null) {
        return result
    }
    throw IllegalArgumentException("$name ignore case not found in $this")
}

@JvmName("valueIgnoreCaseOrNull")
fun <T : Enum<T>> Class<*>.valueOfEnumIgnoreCaseOrNull(name: CharSequence): T? {
    return JavaEnums.valueOfIgnoreCase(this.asAny<Class<T>>(), name.toString()).asAny()
}

@Throws(IndexOutOfBoundsException::class)
@JvmName("value")
fun <T : Enum<T>> Class<*>.valueOfEnum(index: Int): T {
    val result = valueOfEnumOrNull<T>(index)
    if (result !== null) {
        return result
    }
    throw IndexOutOfBoundsException("Index out of bounds in $this: $index")
}

@JvmName("valueOrNull")
fun <T : Enum<T>> Class<*>.valueOfEnumOrNull(index: Int): T? {
    return JavaEnums.indexOf(this.asAny<Class<T>>(), index).asAny()
}