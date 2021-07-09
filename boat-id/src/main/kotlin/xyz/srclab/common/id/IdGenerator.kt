package xyz.srclab.common.id

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.SingleGetter
import xyz.srclab.common.lang.asAny

/**
 * Core interface to generate new id.
 *
 * Use [AbstractIdGenerator] to convenient to implement.
 *
 * Use [IdSpec] to convenient to generate id of [String] type.
 *
 * @author sunqian
 *
 * @see AbstractIdGenerator
 * @see IdSpec
 */
interface IdGenerator<T> {

    /**
     * Returns new id.
     *
     * @return new id
     */
    fun newId(): T
}

/**
 * Abstract [IdGenerator] to help convenient to implement a [IdGenerator].
 *
 * Subclass should provide [components] (and call its [IdComponent.init]) and [joiner].
 */
abstract class AbstractIdGenerator<T> : IdGenerator<T> {

    @get:JvmName("components")
    @Suppress(INAPPLICABLE_JVM_NAME)
    abstract val components: Iterable<IdComponent<Any>>

    @get:JvmName("joiner")
    @Suppress(INAPPLICABLE_JVM_NAME)
    abstract val joiner: IdJoiner<T>

    override fun newId(): T {
        val context = IdContextImpl()
        val componentsValues = context.componentGetters.map { it.get<Any>() }
        return joiner.join(componentsValues)
    }

    private inner class IdContextImpl : IdContext {
        override val componentGetters: List<SingleGetter> = this@AbstractIdGenerator.components.toGetters(this)
    }

    private fun Iterable<IdComponent<Any>>.toGetters(context: IdContext): List<SingleGetter> {
        return this.map {
            object : SingleGetter {

                private var cache: Any? = null

                override fun <T : Any> getOrNull(): T? {
                    val current = cache
                    if (current !== null) {
                        return current.asAny()
                    }
                    val newValue = it.newValue(context)
                    cache = newValue
                    return newValue.asAny()
                }
            }
        }
    }
}