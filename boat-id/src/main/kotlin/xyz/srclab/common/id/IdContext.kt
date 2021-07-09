package xyz.srclab.common.id

import xyz.srclab.common.collect.asToList
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.SingleGetter

/**
 * Context for create id component value.
 *
 * @author sunqian
 *
 * @see IdComponent
 */
interface IdContext {

    @get:JvmName("componentGetters")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val componentGetters: List<SingleGetter>

    companion object {

        @JvmStatic
        fun newContext(componentGetters: Iterable<SingleGetter>): IdContext {
            return object : IdContext {
                override val componentGetters: List<SingleGetter> = componentGetters.asToList()
            }
        }
    }
}