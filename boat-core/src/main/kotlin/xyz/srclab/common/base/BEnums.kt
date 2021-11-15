@file:JvmName("BEnums")

package xyz.srclab.common.base

@Throws(IllegalArgumentException::class)
@JvmName("getValue")
fun <T> Class<*>.enumValue(name: CharSequence): T {
    return enumValueOrNull(name) ?: throw IllegalArgumentException("Value of $this not found: $name")
}

@Throws(IllegalArgumentException::class)
@JvmName("getValueOrNull")
fun <T> Class<*>.enumValueOrNull(name: CharSequence): T? {
    return try {
        JavaEnum.valueOf(this.asTyped<Class<Enum<*>>>(), name.toString()).asTyped()
    } catch (e: Exception) {
        null
    }
}

@Throws(IllegalArgumentException::class)
@JvmName("getValueIgnoreCase")
fun <T> Class<*>.enumValueIgnoreCase(name: CharSequence): T {
    return enumValueIgnoreCaseOrNull(name) ?: throw IllegalArgumentException("Value of $this not found: $name")
}

@Throws(IllegalArgumentException::class)
@JvmName("getValueIgnoreCaseOrNull")
fun <T> Class<*>.enumValueIgnoreCaseOrNull(name: CharSequence): T? {
    val t = enumValueOrNull<T>(name)
    if (t !== null) {
        return t
    }
    val values: Array<out Enum<*>> =
        this.enumConstants.asTyped() ?: throw IllegalArgumentException("Must be an enum type: $this")
    for (value in values) {
        if (value.name.contentEquals(name, true)) {
            return value.asTyped()
        }
    }
    return null
}

@Throws(IndexOutOfBoundsException::class)
@JvmName("getValue")
fun <T> Class<*>.enumValue(index: Int): T {
    return enumValueOrNull(index) ?: throw IllegalArgumentException("Value of $this not found: index $index")
}

@JvmName("getValueOrNull")
fun <T> Class<*>.enumValueOrNull(index: Int): T? {
    val values = this.enumConstants ?: throw IllegalArgumentException("Must be an enum type: $this")
    if (index.isIndexInBounds(0, values.size)) {
        return values[index].asTyped()
    }
    return null
}