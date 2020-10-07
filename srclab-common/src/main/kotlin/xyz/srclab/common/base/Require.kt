@file:JvmName("Require")

package xyz.srclab.common.base

fun <T : Any> T?.notNull(): T {
    Check.checkNull(this != null)
    return this.asNotNull()
}

fun <T : Any> T?.notNull(message: String?): T {
    Check.checkNull(this != null, message)
    return this.asNotNull()
}

fun <T : Any> T?.notNull(messagePattern: String?, vararg messageArgs: Any?): T {
    Check.checkNull(this != null, messagePattern, messageArgs)
    return this.asNotNull()
}

fun <T : Any> T?.notNullElement(): T {
    Check.checkElement(this != null)
    return this.asNotNull()
}

fun <T : Any> T?.notNullElement(message: String?): T {
    Check.checkElement(this != null, message)
    return this.asNotNull()
}

fun <T : Any> T?.notNullElement(messagePattern: String?, vararg messageArgs: Any?): T {
    Check.checkElement(this != null, messagePattern, messageArgs)
    return this.asNotNull()
}

fun <T : Any> T?.notNullElementByKey(key: Any?): T {
    return this.notNullElement("Key: {}.", key)
}