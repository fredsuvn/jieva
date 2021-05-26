package xyz.srclab.common.id

import xyz.srclab.common.lang.CharsTemplate
import xyz.srclab.common.lang.CharsTemplate.Companion.resolveTemplate
import xyz.srclab.common.lang.asAny
import java.util.*

/**
 * Specification implementation of [IdGenerator]. For example:
 * ```
 * String spec = "seq-{Snowflake, 20, 41, 10, 12}-tail";
 * IdSpec idSpec = new IdSpec(spec);
 * ```
 * The result may be:
 * ```
 * seq-06803239610792857600-tail
 * seq-06803239610834800640-tail
 * seq-06803239610834800641-tail
 * seq-06803239610838994944-tail
 * ```
 * An [IdSpec] consists of literals and [IdComponent]s,
 * [IdComponent]s are wrapped between {}, format is {type[: name][, arg]+}
 * (**a comma follows a space or not, note space will be trimmed**).
 *
 * Using `\` to escape `{` out of parameter scope, and escape `}` in parameter scope (with [CharsTemplate] rules):
 * ```
 * seq\{}-{Snowflake, 20, 41, 10, 12}-tail
 * will output:
 * seq{}-202007311734474130000-tail
 *
 * seq\{\}-{Snowflake, 20, 41, 10, 12}\-tail
 * will output:
 * seq{\}-202007311734474130000\-tail
 * ```
 *
 * There are some built-in [IdComponent]s:
 *
 * * [SnowflakeComponent]
 *
 * [IdSpec] will call [IdComponent.init] after creating component,
 * `args` of [IdComponent.init] come from the `spec`,
 * and type of all `args` is [String].
 *
 * @author sunqian
 *
 * @see IdGenerator
 * @see SnowflakeComponent
 */
open class IdSpec @JvmOverloads constructor(
    private val spec: String,
    private val componentSupplier: ComponentSupplier = DEFAULT_COMPONENT_SUPPLIER
) : AbstractIdGenerator<String>() {

    private val template: CharsTemplate = spec.resolveTemplate("{", "}", "\\")

    override val components: Iterable<IdComponent<Any>> = run {

        val result = LinkedList<IdComponent<Any>>()

        fun createParameterComponent(parameter: Any): IdComponent<*> {
            val parameterValue = parameter.toString()
            val split = parameterValue.split(",")
            if (split.isEmpty()) {
                throw IllegalArgumentException("Wrong id spec: $spec")
            }
            val component = componentSupplier.get(split[0].trim())
            val args = split.slice(1 until split.size).map { it.trim() }
            component.init(args)
            return component
        }

        fun createTextComponent(i: Int, any: Any): IdComponent<String> {
            val text = any.toString()
            return object : IdComponent<String> {
                override val type: String = "Text"
                override fun init(args: List<Any>) {}
                override fun newValue(context: IdContext): String = text
            }
        }

        for ((i, node) in template.nodes.withIndex()) {
            if (node.isParameter) {
                result.add(createParameterComponent(node.toParameterNameOrIndex(spec)).asAny())
            } else {
                result.add(createTextComponent(i, node.toText(spec)).asAny())
            }
        }
        result.toList()
    }

    override val joiner: IdJoiner<String> = IdJoiner.STRING_JOINER

    /**
     * Supplier fo [IdComponent].
     */
    interface ComponentSupplier {

        fun get(type: String): IdComponent<*>
    }

    companion object {

        /**
         * Default [ComponentSupplier], supports:
         *
         * * [SnowflakeComponent]
         */
        @JvmField
        val DEFAULT_COMPONENT_SUPPLIER: ComponentSupplier = object : ComponentSupplier {
            override fun get(type: String): IdComponent<*> {
                return when (type) {
                    SnowflakeComponent.TYPE -> SnowflakeComponent()
                    else -> throw IllegalArgumentException("Id component type not found: $type")
                }
            }
        }
    }
}