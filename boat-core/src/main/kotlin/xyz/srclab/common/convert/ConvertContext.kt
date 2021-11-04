package xyz.srclab.common.convert

/**
 * Context of converting.
 */
interface ConvertContext {

    val converter: Converter

    companion object {
        @JvmStatic
        fun newConvertContext(converter: Converter): ConvertContext {
            return object : ConvertContext {
                override val converter: Converter = converter
            }
        }
    }
}