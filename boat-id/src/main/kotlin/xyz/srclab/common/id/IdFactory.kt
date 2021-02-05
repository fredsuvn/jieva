package xyz.srclab.common.id

import xyz.srclab.common.base.asAny

/**
 * Core id generator for boat-id.
 *
 * @author sunqian
 *
 * @see StringIdSpec
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
        fun newStringIdFactory(generators: Iterable<IdComponentGenerator<*>>): StringIdFactory {
            return StringIdFactory(generators)
        }

        @JvmStatic
        @JvmOverloads
        fun newStringIdSpec(
            spec: String,
            specFunction: (args: List<Any?>) -> IdComponentGenerator<*> = DEFAULT_SPEC_FUNCTION
        ): StringIdSpec {
            return StringIdSpec(spec, specFunction)
        }
    }
}

/**
 * Help defining an [IdFactory], which consists of [IdComponentGenerator].
 *
 * Note name of [IdComponentGenerator] should be different.
 */
abstract class AbstractIdFactory<T>(
    private val generators: Iterable<IdComponentGenerator<*>>
) : IdFactory<T> {

    override fun create(): T {
        val components = ArrayList<IdComponent<*>>(generators.count())
        val componentMap = HashMap<String, IdComponent<*>>(components.size)
        val context = Context(componentMap)
        for (generator in generators) {
            val component = IdComponent.newIdComponent(generator, context)
            components.add(component)
            if (componentMap.contains(generator.name)) {
                throw IllegalArgumentException("Conflict with Id component generator name: ${generator.name}")
            }
            componentMap[generator.name] = component
        }
        val values = ArrayList<Any>(components.size)
        for (component in components) {
            values.add(component.get().asAny())
        }
        return concat(values)
    }

    protected abstract fun concat(components: Iterable<Any>): T

    private class Context(private val componentMap: Map<String, IdComponent<*>>) : IdGenerationContext {
        override fun <T> getComponent(name: String): IdComponent<T>? {
            return componentMap[name].asAny()
        }
    }
}

open class StringIdFactory(
    generators: Iterable<IdComponentGenerator<*>>
) : AbstractIdFactory<String>(generators) {
    override fun concat(components: Iterable<Any>): String {
        return components.joinToString(separator = "") { it.toString() }
    }
}