package xyz.srclab.common.id

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME

/**
 *  Id component.
 *
 * @param [E] component type
 * @author sunqian
 */
interface IdComponent<E> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    fun get(): E

    companion object {

        @JvmStatic
        fun <E> newIdComponent(generator: IdComponentGenerator<E>, context: IdGenerationContext): IdComponent<E> {
            return IdComponentImpl(generator, context)
        }
    }
}

private class IdComponentImpl<E>(
    private val generator: IdComponentGenerator<E>,
    private val context: IdGenerationContext,
) : IdComponent<E> {

    private var cache: E? = null

    override val name = generator.name

    override fun get(): E {
        val c = cache
        if (c !== null) {
            return c
        }
        val c0 = generator.generate(context)
        cache = c0
        return c0
    }
}