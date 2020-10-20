@file:JvmName("FieldKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Field

@JvmOverloads
fun Class<*>.findField(name: String, declared: Boolean = false, deep: Boolean = false): Field? {
    var field = try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
    if (field === null && !declared) {
        return null
    }
    field = this.findDeclaredField(name)
    if (field === null && !deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        field = this.findDeclaredField(name)
        if (field !== null) {
            return field
        }
        superClass = superclass.superclass
    }
    return null
}

fun Class<*>.findDeclaredField(name: String): Field? {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

fun Class<*>.findFields(): List<Field> {
    return this.fields.asList()
}

fun Class<*>.findDeclaredFields(): List<Field> {
    return this.declaredFields.asList()
}

@JvmOverloads
fun <T> Field.getValue(owner: Any? = null, force: Boolean = false): T {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.get(owner).asAny()
    } catch (e: IllegalAccessException) {
        throw IllegalStateException(e)
    }
}

@JvmOverloads
fun Field.setValue(value: Any?, owner: Any? = null, force: Boolean = false) {
    try {
        if (force) {
            this.isAccessible = true
        }
        this.set(owner, value)
    } catch (e: IllegalAccessException) {
        throw IllegalStateException(e)
    }
}

@JvmOverloads
fun <T> Class<*>.getFieldValue(name: String, owner: Any? = null, force: Boolean = false, deep: Boolean = false): T {
    val field = this.findField(name, force, deep)
    if (field === null) {
        throw IllegalArgumentException("Field not found: ${this}.${name}")
    }
    return field.getValue(owner, force)
}

@JvmOverloads
fun Class<*>.setFieldValue(
    name: String,
    value: Any?,
    owner: Any? = null,
    force: Boolean = false,
    deep: Boolean = false
) {
    val field = this.findField(name, force, deep)
    if (field === null) {
        throw IllegalArgumentException("Field not found: ${this}.${name}")
    }
    return field.setValue(value, owner, force)
}