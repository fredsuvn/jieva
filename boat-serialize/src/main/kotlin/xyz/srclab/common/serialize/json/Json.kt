package xyz.srclab.common.serialize.json

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.serialize.Serial
import java.lang.reflect.Type

/**
 * Represents a `Json Serial`, can fast convert between literal json and java object.
 *
 * @author sunqian
 */
interface Json : Serial {

    /**
     * Returns type of json.
     *
     * @return type of json
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: JsonType
        @JvmName("type") get

    /**
     * Returns Stringified json content of this [Json].
     * If content is `abs`, returns "abs".
     *
     * @see [toObjectString]
     */
    fun toJsonString(): String

    /**
     * Returns Stringified json content of this [Json].
     * If content is `abs`, returns "abs"'s bytes.
     *
     * @see [toObjectString]
     */
    fun toJsonBytes(): ByteArray

    /**
     * Returns content of this [Json] as [String].
     * If content is `abs`, returns abs.
     *
     * @see [toJsonString]
     */
    fun toObjectString(): String

    /**
     * Must be same as [toJsonString].
     */
    override fun toString(): String

    @JvmDefault
    override fun <T> toObject(type: Type): T {
        return toObjectOrNull<T>(type).orThrow()
    }

    private fun <T> T?.orThrow(): T {
        return this ?: throw IllegalStateException("Null json node, use toObjectOrNull.")
    }
}