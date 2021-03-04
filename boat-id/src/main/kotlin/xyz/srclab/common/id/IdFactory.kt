package xyz.srclab.common.id

import xyz.srclab.common.base.asAny

/**
 * Core factory for create new id.
 *
 * @author sunqian
 *
 * @see StringIdSpec
 * @see AbstractIdFactory
 * @see StringIdFactory
 */
interface IdFactory<T> {

    /**
     * Creates new id.
     *
     * @return new id
     */
    fun create(): T

    companion object {

        @JvmStatic
        fun newStringIdFactory(componentFactories: Iterable<IdComponentFactory<*>>): StringIdFactory {
            return StringIdFactory(componentFactories)
        }
    }
}

/**
 * Skeletal [IdFactory] with an [IdComponentFactory] iterable.
 */
abstract class AbstractIdFactory<T>(
    private val componentFactories: Iterable<IdComponentFactory<*>>
) : IdFactory<T> {

    override fun create(): T {
        val components = ArrayList<IdComponentHolder<*>>(componentFactories.count())
        val context = IdContext.newContext(components)
        for (factory in componentFactories) {
            val component = IdComponentHolder.newIdComponent(factory, context)
            components.add(component)
        }
        val values = ArrayList<Any>(components.size)
        for (component in components) {
            values.add(component.get().asAny())
        }
        return concat(values)
    }

    protected abstract fun concat(components: List<Any>): T
}

/**
 * Skeletal [IdFactory] with an [IdComponentFactory] iterable, build [String] type id.
 */
open class StringIdFactory(
    generators: Iterable<IdComponentFactory<*>>
) : AbstractIdFactory<String>(generators) {
    override fun concat(components: List<Any>): String {
        return components.joinToString(separator = "") { it.toString() }
    }
}