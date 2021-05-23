@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.lang.asAny
import java.lang.reflect.Field

/**
 * @throws NoSuchFieldException
 */
fun Class<*>.field(name: String): Field {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        throw e
    }
}

fun Class<*>.fieldOrNull(name: String): Field? {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

fun Class<*>.fields(): List<Field> {
    return this.fields.asList()
}

/**
 * @throws NoSuchFieldException
 */
fun Class<*>.declaredField(name: String): Field {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        throw e
    }
}

fun Class<*>.declaredFieldOrNull(name: String): Field? {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

fun Class<*>.declaredFields(): List<Field> {
    return this.declaredFields.asList()
}

/**
 * Return public or declared field.
 *
 * @throws NoSuchFieldException
 */
fun Class<*>.ownedField(name: String): Field {
    return ownedFieldOrNull(name) ?: throw NoSuchFieldException(name)
}

/**
 * Return public or declared field.
 */
fun Class<*>.ownedFieldOrNull(name: String): Field? {
    return searchFieldOrNull(name, false)
}

/**
 * Return public and declared fields.
 */
fun Class<*>.ownedFields(): List<Field> {
    val set = LinkedHashSet<Field>()
    set.addAll(this.fields())
    for (declaredField in declaredFields()) {
        if (!set.contains(declaredField)) {
            set.add(declaredField)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchFieldOrNull(name: String, deep: Boolean = true): Field? {
    var field = try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
    if (field !== null) {
        return field
    }
    field = declaredFieldOrNull(name)
    if (field !== null) {
        return field
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        field = superClass.declaredFieldOrNull(name)
        if (field !== null) {
            return field
        }
        superClass = superClass.superclass
    }
    return null
}

@JvmOverloads
fun Class<*>.searchFields(deep: Boolean = true, predicate: (Field) -> Boolean = { true }): List<Field> {
    val result = mutableListOf<Field>()
    for (field in this.fields) {
        if (predicate(field)) {
            result.add(field)
        }
    }
    for (field in this.declaredFields) {
        if (!result.contains(field) && predicate(field)) {
            result.add(field)
        }
    }
    if (!deep) {
        return result
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (field in superClass.declaredFields) {
            if (!result.contains(field) && predicate(field)) {
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
@JvmName("getFieldValue")
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
@JvmName("setFieldValue")
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
@JvmOverloads
fun <T> Class<*>.getFieldValue(name: String, owner: Any?, force: Boolean = false): T {
    val field = field(name)
    return field.getValue(owner, force)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun Class<*>.setFieldValue(name: String, owner: Any?, value: Any?, force: Boolean = false) {
    val field = field(name)
    return field.setValue(value, owner, force)
}

/**
 * Returns values of static field.
 *
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun <T> Class<*>.getStaticFieldValue(name: String, force: Boolean = false): T {
    return getFieldValue(name, null, force)
}

/**
 * Returns values of static field.
 *
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun Class<*>.setStaticFieldValue(name: String, value: Any?, force: Boolean = false) {
    return setFieldValue(name, null, value, force)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun <T> Class<*>.getDeepFieldValue(name: String, owner: Any?, force: Boolean = false): T {
    val field = searchFieldOrNull(name, true)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner, force)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun Class<*>.setDeepFieldValue(name: String, owner: Any?, value: Any?, force: Boolean = false) {
    val field = searchFieldOrNull(name, true)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(owner, value, force)
}