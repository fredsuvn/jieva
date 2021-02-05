package xyz.srclab.common.id

/**
 * Constant component generator.
 *
 * @author sunqian
 */
class ConstantComponentGenerator<E>(
    override val name: String,
    private val value: E,
) : IdComponentGenerator<E> {

    override val type = TYPE

    override fun generate(context: IdGenerationContext): E {
        return value
    }

    companion object {

        const val TYPE = "Constant"
    }
}