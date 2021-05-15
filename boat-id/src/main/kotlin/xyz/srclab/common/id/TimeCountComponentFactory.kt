package xyz.srclab.common.id

import xyz.srclab.common.lang.CharsFormat.Companion.printfFormat
import xyz.srclab.common.lang.Current
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Id component factory to create [TimestampCount].
 *
 * In [StringIdSpec], its name is [StringIdSpec.TIME_COUNT_COMPONENT_FACTORY_NAME] and use 3 arguments:
 * date time pattern, max count and toString pattern. For example:
 * ```
 * String spec = "seq-{timeCount, yyyyMMddHHmmssSSS, 1023, %17s%04d}-tail";
 * StringIdSpec idFactory = new StringIdSpec(spec);
 * ```
 *
 * @author sunqian
 * @see StringIdSpec
 */
class TimeCountComponentFactory(
    dateTimePattern: String?,
    private val maxCount: Long,
    private val toStringPattern: String?,
) : IdComponentFactory<TimestampCount> {

    private val dateTimeFormatter =
        if (dateTimePattern.isNullOrBlank()) null else DateTimeFormatter.ofPattern(dateTimePattern)

    private var lastTime: Long = -1
    private var sequence: Long = 0

    constructor(args: Array<String>) : this(args[0], args[1].toLong(), args[2])

    override fun create(context: IdContext): TimestampCount {
        return synchronized(this) {
            generate0()
        }
    }

    private fun generate0(): TimestampCount {
        val last = lastTime
        val now = Current.millis
        if (now < last) {
            throw IllegalStateException(
                "Clock moved backwards. Refusing to generate id for ${last - now} milliseconds"
            )
        }
        if (now == last) {
            val seq = sequence
            if (seq > maxCount) {
                throw IllegalStateException("Timestamp count overflow: $seq")
            }
            return TimestampCount(now, sequence++, dateTimeFormatter, toStringPattern)
        }
        sequence = 0
        lastTime = now
        return TimestampCount(now, sequence++, dateTimeFormatter, toStringPattern)
    }

    companion object {

        const val NAME = "TimeCount"
    }
}

class TimestampCount(
    @get:JvmName("timestamp") val timestamp: Long,
    @get:JvmName("count") val count: Long,
    @get:JvmName("dateTimeFormatter") val dateTimeFormatter: DateTimeFormatter?,
    @get:JvmName("toStringPattern") val toStringPattern: String?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimestampCount
        if (timestamp != other.timestamp) return false
        if (count != other.count) return false
        if (dateTimeFormatter != other.dateTimeFormatter) return false
        if (toStringPattern != other.toStringPattern) return false
        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + count.hashCode()
        result = 31 * result + (dateTimeFormatter?.hashCode() ?: 0)
        result = 31 * result + (toStringPattern?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        val time = if (dateTimeFormatter === null) {
            timestamp
        } else {
            dateTimeFormatter.format(Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC))
        }
        return if (toStringPattern === null) {
            "$time$count"
        } else {
            toStringPattern.printfFormat(time, count)
        }
    }
}