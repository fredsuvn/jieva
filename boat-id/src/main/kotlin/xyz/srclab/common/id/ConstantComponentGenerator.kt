package xyz.srclab.common.id

/**
 * Constant component generator.
 *
 * @author sunqian
 */
class ConstantComponentGenerator<E>(
    private val value: E,
) : IdComponentGenerator<E> {

    override val name = NAME

    override fun generate(context: IdGenerationContext): E {
        return value
    }

    companion object {

        const val NAME = "Constant"
    }
}