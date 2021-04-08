@file:JvmName("Enums")

package xyz.srclab.common.base

@Throws(IllegalArgumentException::class)
@JvmName("valueOf")
fun <T : Enum<T>> Class<*>.enumValueOf(name: CharSequence): T {
    val result = enumValueOfOrNull<T>(name)
    if (result !== null) {
        return result
    }
    throw IllegalArgumentException("Enum constant $name not found in $this")
}

@JvmName("valueOfOrNull")
fun <T : Enum<T>> Class<*>.enumValueOfOrNull(name: CharSequence): T? {
    return JavaEnums.valueOf(this.asAny<Class<T>>(), name.toString()).asAny()
}

@Throws(IllegalArgumentException::class)
@JvmName("valueOfIgnoreCase")
fun <T : Enum<T>> Class<*>.enumValueOfIgnoreCase(name: CharSequence): T {
    val result = enumValueOfOrNullIgnoreCase<T>(name)
    if (result !== null) {
        return result
    }
    throw IllegalArgumentException("Enum constant ignore case $name not found in $this")
}

@JvmName("valueOfOrNullIgnoreCase")
fun <T : Enum<T>> Class<*>.enumValueOfOrNullIgnoreCase(name: CharSequence): T? {
    return JavaEnums.valueOfIgnoreCase(this.asAny<Class<T>>(), name.toString()).asAny()
}