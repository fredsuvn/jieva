package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.CharsFormat.Companion.fastFormat
import xyz.srclab.common.base.Current
import xyz.srclab.common.egg.sample.Logger
import java.util.*

/**
 * @author sunqian
 */
internal class OSpaceLogger(private val config: OSpaceConfig) : Logger {

    private val _infos = Collections.synchronizedList(LinkedList<OSpaceInfo>())

    val infos: List<OSpaceInfo>
        get() = _infos

    override fun info(pattern: String, vararg args: Any?) {
        //println("${Current.timestamp}-info: " + pattern.fastFormat(*args))
        _infos.add(
            OSpaceInfo(
                pattern.fastFormat(*args),
                Current.timestamp
            )
        )
        if (_infos.size > config.infoListSize) {
            _infos.removeFirst()
        }
    }

    override fun debug(pattern: String, vararg args: Any?) {
        if (config.isDebug) {
            println("${Current.timestamp}-debug: " + pattern.fastFormat(*args))
        }
    }
}

internal data class OSpaceInfo(
    var message: String,
    var timestamp: String,
)