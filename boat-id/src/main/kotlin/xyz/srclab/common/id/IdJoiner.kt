package xyz.srclab.common.id

/**
 * To join id components into one id.
 *
 * @author sunqian
 *
 * @see IdComponent
 */
interface IdJoiner<T> {

    fun join(components: List<Any>): T

    companion object {

        /**
         * Id joiner for [String] type id.
         */
        @JvmField
        val STRING_JOINER: IdJoiner<String> = object : IdJoiner<String> {
            override fun join(components: List<Any>): String {
                return components.joinToString("")
            }
        }
    }
}