package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.CharsFormat.Companion.fastFormat
import xyz.srclab.common.base.Current
import java.util.*

/**
 * @author sunqian
 */
internal object OSpaceLogger {

    private val _infos = Collections.synchronizedList(LinkedList<OSpaceInfo>())

    val infos: List<OSpaceInfo>
        get() = _infos

    fun info(pattern: String, vararg args: Any?) {
        //println("${Current.timestamp}-info: " + pattern.fastFormat(*args))
        _infos.add(
            OSpaceInfo(
                pattern.fastFormat(*args),
                Current.timestamp
            )
        )
        if (_infos.size > Config.infoListSize) {
            _infos.removeFirst()
        }
    }

    fun debug(pattern: String, vararg args: Any?) {
        if (Config.isDebug) {
            println("${Current.timestamp}-debug: " + pattern.fastFormat(*args))
        }
    }
}

internal data class OSpaceInfo(
    var message: String,
    var timestamp: String,
)