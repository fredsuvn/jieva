package xyz.srclab.common.state

interface StringState<T : StringState<T>> : State<String, String, T> {

    companion object {

        fun buildDescription(description: String?, moreDescription: String): String {
            return if (description == null) moreDescription else "$description[$moreDescription]"
        }
    }
}