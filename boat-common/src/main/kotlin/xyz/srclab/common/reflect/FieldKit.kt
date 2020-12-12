@file:JvmName("FieldKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Field

fun Class<*>.findField(name: String): Field? {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

fun Class<*>.findFields(): List<Field> {
    return this.fields.asList()
}

fun Class<*>.findDeclaredField(name: String): Field? {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

fun Class<*>.findDeclaredFields(): List<Field> {
    return this.declaredFields.asList()
}

fun Class<*>.findOwnedField(name: String): Field? {
    return this.searchField(name, false)
}

fun Class<*>.findOwnedFields(): List<Field> {
    val set = LinkedHashSet<Field>()
    set.addAll(this.findFields())
    for (declaredField in this.findDeclaredFields()) {
        if (!set.contains(declaredField)) {
            set.add(declaredField)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchField(name: String, deep: Boolean = true): Field? {
    var field = try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
    if (field !== null) {
        return field
    }
    field = this.findDeclaredField(name)
    if (field !== null) {
        return field
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        field = superClass.findDeclaredField(name)
        if (field !== null) {
            return field
        }
        superClass = superClass.superclass
    }
    return null
}

@JvmOverloads
fun Class<*>.searchFields(deep: Boolean = true, predicate: (Field) -> Boolean): List<Field> {
    val result = mutableListOf<Field>()
    for (field in this.fields) {
        if (predicate(field)) {
            result.add(field)
        }
    }
    for (field in this.declaredFields) {
        if (predicate(field)) {
            result.add(field)
        }
    }
    if (!deep) {
        return result
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (field in superClass.declaredFields) {
            if (predicate(field)) {
                result.add(field)
            }
        }
        superClass = superClass.superclass
    }
    return result
}

/**
 * @throws IllegalAccessException
 */
@JvmOverloads
fun <T> Field.getValue(owner: Any?, force: Boolean = false): T {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.get(owner).asAny()
    } catch (e: IllegalAccessException) {
        throw e
    }
}

/**
 * @throws IllegalAccessException
 */
@JvmOverloads
fun Field.setValue(owner: Any?, value: Any?, force: Boolean = false) {
    try {
        if (force) {
            this.isAccessible = true
        }
        this.set(owner, value)
    } catch (e: IllegalAccessException) {
        throw e
    }
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun <T> Class<*>.getFieldValue(name: String, owner: Any?): T {
    val field = this.findField(name)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner)
}

/**
 * @throws NoSuchFieldException
 */
fun <T> Class<*>.getFieldValue(
    name: String,
    owner: Any?,
    deep: Boolean,
    force: Boolean
): T {
    val field = this.searchField(name, deep)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner, force)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun Class<*>.setFieldValue(name: String, owner: Any?, value: Any?) {
    val field = this.findField(name)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(value, owner)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun Class<*>.setFieldValue(
    name: String,
    owner: Any?,
    value: Any?,
    deep: Boolean,
    force: Boolean,
) {
    val field = this.searchField(name, deep)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(owner, value, force)
}