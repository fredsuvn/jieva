package xyz.srclab.common.id

/**
 * A type of [IdComponent] represents literal text.
 */
class TextComponent(private val value: String) : IdComponent<String> {

    override val type: String = TYPE

    override fun init(args: List<Any>) {
    }

    override fun newValue(context: IdContext): String = value

    companion object {

        const val TYPE = "Text"
    }
}