@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import java.lang.reflect.Member
import java.lang.reflect.Modifier

val Member.isPublic: Boolean
    @JvmName("isPublic") get() {
        return Modifier.isPublic(this.modifiers)
    }

val Member.isPrivate: Boolean
    @JvmName("isPrivate") get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Member.isProtected: Boolean
    @JvmName("isProtected") get() {
        return Modifier.isProtected(this.modifiers)
    }

val Member.isStatic: Boolean
    @JvmName("isStatic") get() {
        return Modifier.isStatic(this.modifiers)
    }

val Member.isFinal: Boolean
    @JvmName("isFinal") get() {
        return Modifier.isFinal(this.modifiers)
    }

val Member.isSynchronized: Boolean
    @JvmName("isSynchronized") get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Member.isVolatile: Boolean
    @JvmName("isVolatile") get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Member.isTransient: Boolean
    @JvmName("isTransient") get() {
        return Modifier.isTransient(this.modifiers)
    }

val Member.isNative: Boolean
    @JvmName("isNative") get() {
        return Modifier.isNative(this.modifiers)
    }

val Member.isInterface: Boolean
    @JvmName("isInterface") get() {
        return Modifier.isInterface(this.modifiers)
    }

val Member.isAbstract: Boolean
    @JvmName("isAbstract") get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Member.isStrict: Boolean
    @JvmName("isStrict") get() {
        return Modifier.isStrict(this.modifiers)
    }

/**
 * Returns whether [clazz] can use [this]
 */
fun Member.isAccessibleFor(clazz: Class<*>): Boolean {
    if (this.isPublic) {
        return true
    }
    if (this.isPrivate) {
        return this.declaringClass == clazz
    }
    val declaringPackage = this.declaringClass.`package`
    if (declaringPackage == clazz.`package`) {
        return true
    }
    if (this.isProtected) {
        return this.declaringClass.isAssignableFrom(clazz)
    }
    return false
}