/**
 * Member utilities.
 */
@file:JvmName("MemberBt")

package xyz.srclab.common.reflect

import java.lang.reflect.Member
import java.lang.reflect.Modifier

val Member.isPublic: Boolean
    get() {
        return Modifier.isPublic(this.modifiers)
    }

val Member.isPrivate: Boolean
    get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Member.isProtected: Boolean
    get() {
        return Modifier.isProtected(this.modifiers)
    }

val Member.isStatic: Boolean
    get() {
        return Modifier.isStatic(this.modifiers)
    }

val Member.isFinal: Boolean
    get() {
        return Modifier.isFinal(this.modifiers)
    }

val Member.isSynchronized: Boolean
    get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Member.isVolatile: Boolean
    get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Member.isTransient: Boolean
    get() {
        return Modifier.isTransient(this.modifiers)
    }

val Member.isNative: Boolean
    get() {
        return Modifier.isNative(this.modifiers)
    }

val Member.isInterface: Boolean
    get() {
        return Modifier.isInterface(this.modifiers)
    }

val Member.isAbstract: Boolean
    get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Member.isStrict: Boolean
    get() {
        return Modifier.isStrict(this.modifiers)
    }

/**
 * Returns whether [this] is accessible for given [request].
 */
fun Member.isAccessibleFor(request: Class<*>): Boolean {
    if (this.isPublic) {
        return true
    }
    if (this.declaringClass == request) {
        return true
    }
    if (this.isPrivate) {
        //member inner class
        return request.declaringClass == this.declaringClass && !request.isStatic
    }
    if (this.declaringClass.`package` == request.`package`) {
        return true
    }
    if (this.isProtected) {
        return this.declaringClass.isAssignableFrom(request)
    }
    return false
}