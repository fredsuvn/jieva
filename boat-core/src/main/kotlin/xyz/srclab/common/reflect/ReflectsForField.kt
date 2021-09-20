@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Field

/**
 * @throws NoSuchFieldException
 */
@JvmName("getField")
fun Class<*>.field(name: String): Field {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        throw e
    }
}

@JvmName("getFieldOrNull")
fun Class<*>.fieldOrNull(name: String): Field? {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

@JvmName("getFields")
fun Class<*>.fields(): List<Field> {
    return this.fields.asList()
}

/**
 * @throws NoSuchFieldException
 */
@JvmName("getDeclaredField")
fun Class<*>.declaredField(name: String): Field {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        throw e
    }
}

@JvmName("getDeclaredFieldOrNull")
fun Class<*>.declaredFieldOrNull(name: String): Field? {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

@JvmName("getDeclaredFields")
fun Class<*>.declaredFields(): List<Field> {
    return this.declaredFields.asList()
}

/**
 * Return public or declared field.
 *
 * @throws NoSuchFieldException
 */
@JvmName("getOwnedField")
fun Class<*>.ownedField(name: String): Field {
    return fieldOrNull(name) ?: declaredField(name)
}

/**
 * Return public or declared field.
 */
@JvmName("getOwnedFieldOrNull")
fun Class<*>.ownedFieldOrNull(name: String): Field? {
    return fieldOrNull(name) ?: declaredFieldOrNull(name)
}

/**
 * Return declared and public fields.
 */
@JvmName("getOwnedFields")
fun Class<*>.ownedFields(): List<Field> {
    val set = LinkedHashSet<Field>()
    set.addAll(this.declaredFields)
    for (field in this.fields) {
        if (!set.contains(field)) {
            set.add(field)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchFieldOrNull(name: String, deep: Boolean = true): Field? {
    var field = ownedFieldOrNull(name)
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
fun <C : MutableCollection<in Field>> Class<*>.searchFields(
    destination: C,
    deep: Boolean = true,
    predicate: (Field) -> Boolean = { true }
): C {
    for (field in this.fields) {
        if (predicate(field)) {
            destination.add(field)
        }
    }
    for (field in this.declaredFields) {
        if (!destination.contains(field) && predicate(field)) {
            destination.add(field)
        }
    }
    if (!deep) {
        return destination
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (field in superClass.declaredFields) {
            if (!destination.contains(field) && predicate(field)) {
                destination.add(field)
            }
        }
        superClass = superClass.superclass
    }
    return destination
}

/**
 * @throws IllegalAccessException
 */
@JvmName("getFieldValue")
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
@JvmName("setFieldValue")
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
@JvmOverloads
fun <T> Class<*>.getFieldValue(name: String, owner: Any?, force: Boolean = false): T {
    val field = if (force) ownedField(name) else field(name)
    return field.getValue(owner, force)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun Class<*>.setFieldValue(name: String, owner: Any?, value: Any?, force: Boolean = false) {
    val field = if (force) ownedField(name) else field(name)
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
 * Uses [searchFieldOrNull] to find deep field and gets its value.
 *
 * @throws NoSuchFieldException
 */
fun <T> Class<*>.getDeepFieldValue(name: String, owner: Any?): T {
    val field = searchFieldOrNull(name, true)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner, true)
}

/**
 * Uses [searchFieldOrNull] to find deep field and sets its value.
 *
 * @throws NoSuchFieldException
 */
fun Class<*>.setDeepFieldValue(name: String, owner: Any?, value: Any?) {
    val field = searchFieldOrNull(name, true)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(owner, value, true)
}