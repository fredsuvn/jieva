package xyz.srclab.common.id

/**
 * Context of Id factory.
 *
 * @author sunqian
 */
interface IdContext {

    val components: List<IdComponentHolder<*>>

    companion object {

        @JvmStatic
        fun newContext(components: List<IdComponentHolder<*>>): IdContext {
            return object : IdContext {
                override val components: List<IdComponentHolder<*>> = components
            }
        }
    }
}