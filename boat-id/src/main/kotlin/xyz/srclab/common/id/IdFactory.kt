package xyz.srclab.common.id

import xyz.srclab.common.base.asAny

/**
 * Factory for create new Id.
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
    fun newId(): T

    companion object {

        @JvmStatic
        fun newStringIdFactory(generators: Iterable<IdComponentGenerator<*>>): StringIdFactory {
            return StringIdFactory(generators)
        }

        @JvmStatic
        @JvmOverloads
        fun newStringIdSpec(
            spec: String,
            factoryGenerator: StringIdFactoryGenerator = StringIdFactoryGenerator.DEFAULT
        ): StringIdSpec {
            return StringIdSpec(spec, factoryGenerator)
        }
    }
}

abstract class AbstractIdFactory<T>(
    private val generators: Iterable<IdComponentGenerator<*>>
) : IdFactory<T> {

    override fun newId(): T {
        val components = ArrayList<IdComponent<*>>(generators.count())
        val context = Context(components)
        for (generator in generators) {
            val component = IdComponent.newIdComponent(generator, context)
            components.add(component)
        }
        val values = ArrayList<Any>(components.size)
        for (component in components) {
            values.add(component.get().asAny())
        }
        return concat(values)
    }

    protected abstract fun concat(components: List<Any>): T

    private class Context(private val components: List<IdComponent<*>>) : IdGenerationContext {
        override fun <T> components(): List<IdComponent<T>> {
            return components()
        }
    }
}

open class StringIdFactory(
    generators: Iterable<IdComponentGenerator<*>>
) : AbstractIdFactory<String>(generators) {
    override fun concat(components: List<Any>): String {
        return components.joinToString(separator = "") { it.toString() }
    }
}