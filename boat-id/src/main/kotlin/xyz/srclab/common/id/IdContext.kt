package xyz.srclab.common.id

import xyz.srclab.common.collect.asToList
import xyz.srclab.common.lang.Getter
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME

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
    val componentGetters: List<Getter>

    companion object {

        @JvmStatic
        fun newContext(componentGetters: Iterable<Getter>): IdContext {
            return object : IdContext {
                override val componentGetters: List<Getter> = componentGetters.asToList()
            }
        }
    }
}