package xyz.srclab.common.egg.sample

import xyz.srclab.common.egg.Egg

/**
 * Data of [Egg].
 *
 * @author sunqian
 */
interface Data<S : Scenario> {

    val scenario: S

    fun save(path: CharSequence)
}