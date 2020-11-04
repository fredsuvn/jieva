@file:JvmName("JReflect")

package xyz.srclab.jvm.reflect

fun Class<*>.findCallerFrame(): StackTraceElement? {
    val stackTrace = Throwable().stackTrace
    if (stackTrace.isNullOrEmpty()) {
        return null
    }
    val calledClassName = this.name
    var calledIndex = 0
    for (i in stackTrace.indices) {
        if (stackTrace[i].className == calledClassName) {
            calledIndex = i
            break
        }
    }
    for (i in calledIndex until stackTrace.size) {
        if (stackTrace[i].className != calledClassName) {
            return stackTrace[i]
        }
    }
    return null
}