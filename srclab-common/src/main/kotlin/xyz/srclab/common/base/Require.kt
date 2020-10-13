@file:JvmName("Require")
@file:JvmMultifileClass

package xyz.srclab.common.base

fun <T : Any> T?.notNull(): T {
    (this !== null).checkNull()
    return this.asNotNull()
}

fun <T : Any> T?.notNull(message: String?): T {
    (this !== null).checkNull(message)
    return this.asNotNull()
}

fun <T : Any> T?.notNull(messagePattern: String?, vararg messageArgs: Any?): T {
    (this !== null).checkNull(messagePattern, messageArgs)
    return this.asNotNull()
}

fun <T : Any> T?.notNullElement(): T {
    (this !== null).checkElement()
    return this.asNotNull()
}

fun <T : Any> T?.notNullElement(message: String?): T {
    (this !== null).checkElement(message)
    return this.asNotNull()
}

fun <T : Any> T?.notNullElement(messagePattern: String?, vararg messageArgs: Any?): T {
    (this !== null).checkElement(messagePattern, messageArgs)
    return this.asNotNull()
}

fun <T : Any> T?.notNullElementByKey(key: Any?): T {
    return this.notNullElement("Key: {}.", key)
}