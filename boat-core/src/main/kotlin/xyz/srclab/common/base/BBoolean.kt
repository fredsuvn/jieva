@file:JvmName("BBoolean")

package xyz.srclab.common.base

fun CharSequence?.toBoolean(): Boolean {
    return this.contentEquals("true", true)
}

fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
        else -> this.toCharSeq().toBoolean()
    }
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

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

fun Boolean?.toInt(): Int {
    return if (this !== null && this) 1 else 0
}