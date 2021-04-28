package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.CharsFormat.Companion.fastFormat
import xyz.srclab.common.base.Current
import java.util.*

/**
 * @author sunqian
 */
internal object OLogger {

    private val messages = Collections.synchronizedList(LinkedList<OLogMessage>())

    val infos: List<OLogMessage>
        get() = messages

    fun info(pattern: String, vararg args: Any?) {
        //println("${Current.timestamp}-info: " + pattern.fastFormat(*args))
        messages.add(
            OLogMessage(
                pattern.fastFormat(*args),
                Current.timestamp
            )
        )
        if (messages.size > OConfig.infoListSize) {
            messages.removeFirst()
        }
    }

    fun debug(pattern: String, vararg args: Any?) {
        if (OConfig.isDebug) {
            println("${Current.timestamp}-debug: " + pattern.fastFormat(*args))
        }
    }

    internal data class OLogMessage(
        var message: String,
        var timestamp: String,
    )
}