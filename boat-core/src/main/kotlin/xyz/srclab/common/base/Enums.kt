@file:JvmName("Enums")

package xyz.srclab.common.base

@Throws(IllegalArgumentException::class)
@JvmName("getValue")
fun <T : Enum<T>> Class<T>.enumValue(name: CharSequence): T {
    return enumValueOrNull(name) ?: throw IllegalArgumentException("$name not found in $this")
}

@Throws(IllegalArgumentException::class)
@JvmName("getValueOrNull")
fun <T : Enum<T>> Class<T>.enumValueOrNull(name: CharSequence): T? {
    return JavaEnum.valueOf(this, name.toString()).asAny()
}

@Throws(IllegalArgumentException::class)
@JvmName("getValueIgnoreCase")
fun <T : Enum<T>> Class<T>.enumValueIgnoreCase(name: CharSequence): T {
    return enumValueIgnoreCaseOrNull(name) ?: throw IllegalArgumentException("$name not found in $this")
}

@Throws(IllegalArgumentException::class)
@JvmName("getValueIgnoreCaseOrNull")
fun <T : Enum<T>> Class<T>.enumValueIgnoreCaseOrNull(name: CharSequence): T? {
    val t = enumValueOrNull(name)
    if (t !== null) {
        return t
    }
    val values = this.enumConstants ?: throw IllegalArgumentException("Must be an enum type: $this")
    for (value in values) {
        if (value.name.contentEquals(name, true)) {
            return value
        }
    }
    return null
}

@Throws(IndexOutOfBoundsException::class)
@JvmName("getValue")
fun <T : Enum<T>> Class<T>.enumValue(index: Int): T {
    return enumValueOrNull(index) ?: throw IllegalArgumentException("$name not found in $this")
}

@JvmName("getValueOrNull")
fun <T : Enum<T>> Class<T>.enumValueOrNull(index: Int): T? {
    val values = this.enumConstants ?: throw IllegalArgumentException("Must be an enum type: $this")
    if (isIndexInBounds(index, 0, values.size)) {
        return values[index]
    }
    return null
}