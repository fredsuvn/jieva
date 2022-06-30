/**
 * Field utilities.
 */
@file:JvmName("FieldBt")

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asType
import java.lang.reflect.Field
import java.util.function.Predicate

/**
 * Returns public field of [this] class.
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

/**
 * Returns public field of [this] class, or null if failed.
 */
@JvmName("getFieldOrNull")
fun Class<*>.fieldOrNull(name: String): Field? {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

/**
 * Returns all public fields of [this] class.
 */
@JvmName("getFields")
fun Class<*>.fields(): List<Field> {
    return this.fields.asList()
}

/**
 * Returns declared field of [this] class.
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

/**
 * Returns declared field of [this] class, or null if failed.
 */
@JvmName("getDeclaredFieldOrNull")
fun Class<*>.declaredFieldOrNull(name: String): Field? {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

/**
 * Returns declared public fields of [this] class.
 */
@JvmName("getDeclaredFields")
fun Class<*>.declaredFields(): List<Field> {
    return this.declaredFields.asList()
}

/**
 * Return public or declared field of [this] class.
 *
 * @throws NoSuchFieldException
 */
@JvmName("getOwnedField")
fun Class<*>.ownedField(name: String): Field {
    return fieldOrNull(name) ?: declaredField(name)
}

/**
 * Return public or declared field of [this] class., or null if failed.
 */
@JvmName("getOwnedFieldOrNull")
fun Class<*>.ownedFieldOrNull(name: String): Field? {
    return fieldOrNull(name) ?: declaredFieldOrNull(name)
}

/**
 * Return all declared and public fields of [this] class.
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

/**
 * Searches and returns field for [this] class.
 *
 * @param name field name
 * @param deep whether recursively search to super class
 */
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

/**
 * Searches and returns fields for [this] class.
 *
 * @param deep whether recursively search to super class
 * @param predicate true if the field is matched and will be return
 */
@JvmOverloads
fun Class<*>.searchFields(
    deep: Boolean = true,
    predicate: Predicate<Field> = Predicate { true }
): List<Field> {
    return searchFieldsTo(ArrayList(), deep, predicate)
}

/**
 * Searches and returns fields for [this] class into [destination].
 *
 * @param deep whether recursively search to super class
 * @param predicate true if the field is matched and will be return
 */
@JvmOverloads
fun <C : MutableCollection<in Field>> Class<*>.searchFieldsTo(
    destination: C,
    deep: Boolean = true,
    predicate: Predicate<Field> = Predicate { true }
): C {
    for (field in this.fields) {
        if (predicate.test(field)) {
            destination.add(field)
        }
    }
    for (field in this.declaredFields) {
        if (!destination.contains(field) && predicate.test(field)) {
            destination.add(field)
        }
    }
    if (!deep) {
        return destination
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (field in superClass.declaredFields) {
            if (!destination.contains(field) && predicate.test(field)) {
                destination.add(field)
            }
        }
        superClass = superClass.superclass
    }
    return destination
}

/**
 * Gets value of [this] field.
 * @throws IllegalAccessException
 */
@JvmName("getFieldValue")
@JvmOverloads
fun <T> Field.getValue(owner: Any?, force: Boolean = false): T {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.get(owner).asType()
    } catch (e: IllegalAccessException) {
        throw e
    }
}

/**
 * Sets value of [this] field.
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
 * Gets value of field.
 *
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun <T> Class<*>.getFieldValue(name: String, owner: Any?, force: Boolean = false): T {
    val field = if (force) ownedField(name) else field(name)
    return field.getValue(owner, force)
}

/**
 * Sets value of field.
 *
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
 * @throws NoSuchFieldException
 */
fun Class<*>.setDeepFieldValue(name: String, owner: Any?, value: Any?) {
    val field = searchFieldOrNull(name, true)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(owner, value, true)
}