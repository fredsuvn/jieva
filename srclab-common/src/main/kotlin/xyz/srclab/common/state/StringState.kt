package xyz.srclab.common.state

interface StringState<T : StringState<T>> : State<String, String, T> {

    companion object {

        @JvmStatic
        fun moreDescription(description: String?, moreDescription: String?): String? {
            return if (description == null)
                moreDescription
            else
                "$description[$moreDescription]"
        }
    }
}

fun String?.moreStateDescription(moreDescription: String?): String? {
    return StringState.moreDescription(this, moreDescription)
}