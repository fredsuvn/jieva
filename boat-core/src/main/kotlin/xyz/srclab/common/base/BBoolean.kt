@file:JvmName("BBoolean")

package xyz.srclab.common.base

import kotlin.text.toBoolean as toBooleanKt

fun CharSequence?.toBoolean(): Boolean {
    return this?.toString().toBooleanKt()
}

fun anyTrue(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.toBoolean()) {
            return true
        }
    }
    return false
}

fun anyTrue(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.toBoolean()) {
            return true
        }
    }
    return false
}

fun allTrue(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.toBoolean()) {
            return false
        }
    }
    return true
}

fun allTrue(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.toBoolean()) {
            return false
        }
    }
    return true
}

fun anyFalse(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.toBoolean()) {
            return true
        }
    }
    return false
}

fun anyFalse(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.toBoolean()) {
            return true
        }
    }
    return false
}

fun allFalse(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.toBoolean()) {
            return false
        }
    }
    return true
}

fun allFalse(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.toBoolean()) {
            return false
        }
    }
    return true
}