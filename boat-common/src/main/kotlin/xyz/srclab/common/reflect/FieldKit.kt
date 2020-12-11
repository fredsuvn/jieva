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
    return this.searchField(name, declared = true, deep = false)
}

fun Class<*>.findOwnedFields(): List<Field> {
    val set = LinkedHashSet<Field>()
    set.addAll(this.findFields())
    val declared = this.findDeclaredFields()
    for (field in declared) {
        if (!set.contains(field)) {
            set.add(field)
        }
    }
    var superClass = this.superclass
    while (superClass !== null) {
        val superFields = superClass.findDeclaredFields()
        for (superField in superFields) {
            if (superField.isProtected && !set.contains(superField)) {
                set.add(superField)
            }
        }
        superClass = superClass.superclass
    }
    return set.toList()
}

fun Class<*>.searchField(name: String, declared: Boolean, deep: Boolean): Field? {
    var field = try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
    if (field !== null) {
        return field
    }
    if (!declared) {
        return null
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
    declared: Boolean,
    deep: Boolean,
    force: Boolean
): T {
    val field = this.searchField(name, declared, deep)
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
    declared: Boolean,
    deep: Boolean,
    force: Boolean,
) {
    val field = this.searchField(name, declared, deep)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(owner, value, force)
}