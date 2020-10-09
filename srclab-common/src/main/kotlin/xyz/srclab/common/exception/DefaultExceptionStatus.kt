package xyz.srclab.common.exception

enum class DefaultExceptionStatus(code: String, description: String?) : ExceptionStatus {

    INTERNAL("000000", "Internal Error"),
    UNKNOWN("000001", "Unknown Error"),
    ;

    private val instance: ExceptionStatus = ExceptionStatus.newInstance(code, description)

    override fun code(): String {
        return instance.code()
    }

    override fun description(): String? {
        return instance.description()
    }

    override fun toString(): String {
        return instance.toString()
    }
}