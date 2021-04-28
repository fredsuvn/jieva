package xyz.srclab.common.egg

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME

/**
 * @author sunqian
 */
interface Egg {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("shell")
    val shell: String

    fun hatchOut(spell: CharSequence)
}